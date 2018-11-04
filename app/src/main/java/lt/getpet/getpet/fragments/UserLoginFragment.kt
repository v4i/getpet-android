package lt.getpet.getpet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_user_login.*
import lt.getpet.getpet.R
import lt.getpet.getpet.data.Provider
import lt.getpet.getpet.data.RegularUser
import lt.getpet.getpet.navigation.NavigationManager
import timber.log.Timber
import javax.inject.Inject

class UserLoginFragment : BaseFragment() {

    @Inject
    lateinit var navigationManager: NavigationManager

    private var callback: UserLoginCallback? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        callback = activity as UserLoginCallback


        button_facebook.setOnClickListener {
            navigationManager.navigateToUserLoginActivity(activity!!)
        }

        button_google.setOnClickListener {
            signInWithGoogle()
        }

    }

    override fun onStart() {
        super.onStart()

        silentlySignInWithGoogle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SIGN_IN -> signInWithGoogleWithIntent(data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun silentlySignInWithGoogle() {
        val existingAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (existingAccount != null) {
            signInWithGoogleAccount(existingAccount)
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        val existingAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (existingAccount != null) {
            signInWithGoogleAccount(existingAccount)
        } else {
            val client = GoogleSignIn.getClient(context!!, gso)
            startActivityForResult(client.signInIntent, RC_SIGN_IN)
        }
    }

    private fun signInWithGoogleWithIntent(data: Intent?) {
        try {
            val accountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data)
            signInWithGoogleAccount(accountFromIntent.getResult(ApiException::class.java)!!)
        } catch (error: ApiException) {
            Timber.e(error)
        }
    }


    private fun signInWithGoogleAccount(account: GoogleSignInAccount) {
        val userAccount = RegularUser(
                name = account.displayName!!,
                email = account.email!!,
                photo_url = account.photoUrl?.toString(),
                provider = Provider.GOOGLE
        )
        callback?.onUserLoggedIn(userAccount)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_user_login, container, false)
    }


    interface UserLoginCallback {
        fun onUserLoggedIn(regularUser: RegularUser)
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserLoginFragment()

        private const val RC_SIGN_IN = 1001
    }
}