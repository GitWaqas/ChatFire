package com.example.message.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.message.R
import com.example.message.activity.SignInActivity
import com.example.message.glide.GlideApp
import com.example.message.controller.FireStoreController
import com.example.message.controller.StorageController
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.io.ByteArrayOutputStream


class MySettingsFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/gif"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }

            btn_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageController.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FireStoreController.updateCurrentUser(
                            editText_name.text.toString(),
                            editText_bio.text.toString(), imagePath
                        )
                    }
                else
                    FireStoreController.updateCurrentUser(
                        editText_name.text.toString(),
                        editText_bio.text.toString(), null
                    )
                toast("Saving")
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance()
                    .signOut(this@MySettingsFragment.context!!)
                    .addOnCompleteListener {
                        startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
            }

            btn_delete.setOnClickListener {
                alert(context.getString(R.string.warning)) {
                    title = context.getString(R.string.question)
                    yesButton {
                        AuthUI.getInstance()
                            .delete(this@MySettingsFragment.context!!)
                            .addOnCompleteListener {
                                startActivity(intentFor<SignInActivity>().newTask().clearTask())
                                toast("Wiped")
                            }
                    }
                    noButton { it.dismiss() }
                }.show()


            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FireStoreController.getCurrentUser { user ->
            if (this@MySettingsFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if (!pictureJustChanged && user.avatarPath != null)
                    GlideApp.with(this)
                        .load(StorageController.pathToReference(user.avatarPath))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(imageView_profile_picture)
            }
        }
    }


}
