package uz.jagito.todoandnote.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_data_dialog.*
import kotlinx.android.synthetic.main.user_data_dialog.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.UserData
import uz.jagito.todoandnote.utils.extensions.SingleBlock


class EditProfileDialog(context: Context) : AlertDialog(context) {

    private var imagePath = ""
    private var setData: SingleBlock<UserData>? = null
    private var onClickChargeAvatar: ((t: View) -> Unit)? = null
    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.user_data_dialog, null, false)


    init {
        setView(contentView)
        setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", null) { _, _ -> }
        setButton(DialogInterface.BUTTON_POSITIVE, "Save", null) { _, _ ->
            val name = contentView.userName.text.toString()
            val email = contentView.emailUser.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(context, "Please, enter your name!", Toast.LENGTH_SHORT).show()
            } else {
                setData?.invoke(UserData(name, email, imagePath))
            }
        }

        contentView.changeAvatar.setOnClickListener {
            onClickChargeAvatar?.invoke(it)
        }

    }

    fun getUserData(f: SingleBlock<UserData>) {
        setData = f
    }

    fun setUserData(userData: UserData) {
        imagePath = userData.pathOfAvatarImage
        contentView.userName.setText(userData.name)
        contentView.emailUser.setText(userData.email)
        val picasso = Picasso.get()
        picasso.load(userData.pathOfAvatarImage.toUri())
            .rotate(90f).error(R.drawable.ic_account)
            .into(contentView.imageAccountDialog)
    }

    fun setImage(uri: Uri) {
        imageAccountDialog.setImageURI(uri)
        imagePath = uri.toString()
    }

    fun setOnChargeAvatar(t: (View) -> Unit) {
        onClickChargeAvatar = t
    }
}
