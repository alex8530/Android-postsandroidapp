
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.postsandroidapp.R

import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

fun Fragment.showToast(string: String) {
    Toast.makeText(this.activity, string, Toast.LENGTH_LONG).show()
}


fun Fragment.freezUi() {

    this.activity?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Fragment.releaseUi() {
    this.activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}


fun ImageView.loadPicassoImage(urlImage: Any) {

    if (urlImage is String) {

        Picasso
            .get()
            .load(urlImage)
            .placeholder(R.color.dark_gray) // can also be a drawable
            .error(R.color.red) // will be displayed if the image cannot be loaded
            .into(this)

    } else if (urlImage is Int) {

        Picasso
            .get()
            .load(urlImage)
            .placeholder(R.color.dark_gray) // can also be a drawable
            .error(R.color.red) // will be displayed if the image cannot be loaded
            .into(this)
    }

}

fun CircleImageView.loadPicassoImage(urlImage: String) {
    Picasso
        .get()
        .load(urlImage)
        .placeholder(R.color.dark_gray) // can also be a drawable
        .error(R.color.red) // will be displayed if the image cannot be loaded
        .into(this)
}


fun Fragment.navigateTo(fromFragmentId: Int, toAction: Int) {
    if (findNavController().currentDestination?.id == fromFragmentId) {
        findNavController().navigate(toAction)
    }
}

fun Fragment.popNavigation() {

    findNavController().popBackStack()
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}