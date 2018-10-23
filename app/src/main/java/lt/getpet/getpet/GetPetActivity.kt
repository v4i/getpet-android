package lt.getpet.getpet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_get_pet.*
import lt.getpet.getpet.R
import lt.getpet.getpet.data.Pet

class GetPetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_pet)


        val pet = intent.getParcelableExtra<Pet>("pet")

        Glide.with(this).load(pet.photo).into(get_pet_image)
        get_pet_name.text = pet.name
        get_pet_short_description.text = pet.short_description


        shelter_name.text = pet.shelter.name
        shelter_email.text = pet.shelter.email
        shelter_phone.text = pet.shelter.phone
    }
}