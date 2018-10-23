package lt.getpet.getpet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import lt.getpet.getpet.network.PetApiService
import lt.getpet.getpet.persistence.PetsDatabase
import timber.log.Timber


class SplashActivity : AppCompatActivity() {

    private var subscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        subscription = PetApiService.create().getPets()
                .subscribeOn(Schedulers.io())
                .map { pets ->
                    PetsDatabase.getInstance(this).petsDao().insertPets(pets)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showMainActivity()
                }, { t ->
                    Timber.w(t)
                    Toast.makeText(this, "Error loading pets", Toast.LENGTH_LONG).show()
                })
    }

    private fun showMainActivity() {
        val loadMainActivity = Intent(this@SplashActivity, MainActivity::class.java)

        startActivity(loadMainActivity)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.dispose()

    }
}
