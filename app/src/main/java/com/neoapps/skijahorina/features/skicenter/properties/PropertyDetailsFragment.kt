package com.neoapps.skijahorina.features.skicenter.properties

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.databinding.FragmentPropertyDetailsBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.neoapps.skijahorina.main.MainActivity

class PropertyDetailsFragment : Fragment() {

    private var _binding: FragmentPropertyDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var propertyViewModel: PropertyViewModel
    private lateinit var propertyFirst: Property

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)
        setUpFragmentName()

        storage = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance("gs://ski-jahorina.firebasestorage.app").reference.child("properties")

        propertyViewModel = ViewModelProvider(this)[PropertyViewModel::class.java]
        propertyViewModel.getAllProperties { properties ->
            if (properties.isNotEmpty()) {
                propertyFirst = properties.first()

                binding.textViewApartmentName.text = propertyFirst.name
                binding.textViewLocation.text = propertyFirst.location
                binding.textViewDescriptionContent.text = propertyFirst.description

                loadImagesFromStorage(propertyFirst.pictures)
                setTheVisibilityOfTheBasicContacts(propertyFirst)
            }
        }

        binding.cardViewEmail.setOnClickListener {
            val defaultEmail = getString(R.string.default_email_property)
            val recipientEmail = if (::propertyFirst.isInitialized && propertyFirst.email.isNotBlank()) {
                propertyFirst.email
            } else {
                defaultEmail
            }

            val subject = resources.getString(R.string.default_subject)

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$recipientEmail")
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }

            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                showSnackbar(resources.getString(R.string.property_email_app_validation))
            }
        }

        binding.cardViewPhoneNumber.setOnClickListener {
            val defaultNumber = getString(R.string.default_number_property)
            val phoneNumber = if (::propertyFirst.isInitialized && propertyFirst.phoneNumber.isNotBlank()) {
                propertyFirst.phoneNumber
            } else {
                defaultNumber
            }

            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialIntent)
        }

        binding.cardViewWebsite.setOnClickListener {
            val defaultWebsite = getString(R.string.default_website_property)
            val website = if (::propertyFirst.isInitialized && propertyFirst.website.isNotBlank()) {
                propertyFirst.website
            } else {
                defaultWebsite
            }


            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(website)
            )
            startActivity(browserIntent)
        }



        return binding.root
    }

    private fun loadImagesFromStorage(searchString: String) {
        val imageNames = searchString.split(",").map { it.trim() }
        val imageViewMap = listOf(
            binding.imageViewPic1 to binding.cardViewApartmentPic1,
            binding.imageViewPic2 to binding.cardViewApartmentPic2,
            binding.imageViewPic3 to binding.cardViewApartmentPic3,
            binding.imageViewPic4 to binding.cardViewApartmentPic4
        )

        val maxImagesToShow = 4
        val requiredImageNames = imageNames.take(maxImagesToShow)
        val extraImageNames = imageNames.drop(maxImagesToShow)

        val imageUris = Array<String?>(imageNames.size) { null }

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                val itemsByName = listResult.items.associateBy { it.name.substringBeforeLast(".") }

                requiredImageNames.forEachIndexed { index, imageName ->
                    val (imageView, cardView) = imageViewMap[index]
                    val item = itemsByName[imageName]

                    if (item != null) {
                        item.downloadUrl.addOnSuccessListener { uri ->
                            Log.d("PropertyDetailsFragment", "Uri : $uri")
                            Glide.with(this)
                                .load(uri)
                                .into(imageView)

                            imageUris[index] = uri.toString()

                            cardView.visibility = View.VISIBLE

                            imageView.setOnClickListener {
                                Log.d("PropertyDetailsFragment", "Navigating to full screen with images: ${imageUris.filterNotNull()}")
                                navigateToFullScreenFragment(imageUris.filterNotNull(), index)
                            }
                        }.addOnFailureListener {
                            Log.e("PropertyDetailsFragment", "Failed to get download URL for ${item.name}")
                            cardView.visibility = View.GONE
                        }
                    } else {
                        Log.e("PropertyDetailsFragment", "No matching item found for name: $imageName")
                        cardView.visibility = View.GONE
                    }
                }

                for (i in requiredImageNames.size until maxImagesToShow) {
                    imageViewMap[i].second.visibility = View.GONE
                }

                if (extraImageNames.isNotEmpty()) {
                    binding.textViewForImageView4.apply {
                        text = "+${extraImageNames.size}"
                        visibility = View.VISIBLE
                    }
                    binding.cardViewApartmentPic4.visibility = View.VISIBLE
                    binding.imageViewPic4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.propertyImage4Shadowed))
                } else {
                    binding.textViewForImageView4.visibility = View.GONE
                    binding.cardViewApartmentPic4.visibility = View.GONE
                    binding.imageViewPic4.clearColorFilter()
                }

                // Load extra images beyond the first 4
                extraImageNames.forEachIndexed { index, imageName ->
                    val item = itemsByName[imageName]

                    if (item != null) {
                        item.downloadUrl.addOnSuccessListener { uri ->
                            Log.d("PropertyDetailsFragment", "Extra Uri : $uri")
                            imageUris[maxImagesToShow + index] = uri.toString()
                        }.addOnFailureListener {
                            Log.e("PropertyDetailsFragment", "Failed to get download URL for ${item.name}")
                        }
                    } else {
                        Log.e("PropertyDetailsFragment", "No matching item found for name: $imageName")
                    }
                }
            }
            .addOnFailureListener {
                Log.e("PropertyDetailsFragment", "Failed to list files in storage")
            }
    }

    private fun navigateToFullScreenFragment(imageUrls: List<String>, initialPosition: Int) {
        val imageUrlString = imageUrls.joinToString(",")
        Log.d("PropertyDetailsFragment", "Navigating to full screen with images: $imageUrlString")
        val action = PropertyDetailsFragmentDirections.actionFullScreenPropertyImageFragment(
            imageUrlString,
            initialPosition
        )
        findNavController().navigate(action)
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        val snackbarLayout: View = snackbar.view
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        val textView = snackbarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_about_snackbar, 0, 0, 0)
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.snackbar_warning)
        snackbar.show()
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getString(R.string.property_details)
        }
    }

    private fun setTheVisibilityOfTheBasicContacts(property: Property) {
        if (property.email.isEmpty()) {
            binding.textViewEmailContent.visibility = View.GONE
            binding.cardViewEmail.visibility = View.GONE
            binding.emailIcon.visibility = View.GONE
            binding.textViewEmailLabel.visibility = View.GONE
        } else {
            binding.textViewEmailContent.text = property.email
        }

        if (property.phoneNumber.isEmpty()) {
            binding.textViewPhoneContent.visibility = View.GONE
            binding.cardViewPhoneNumber.visibility = View.GONE
            binding.phoneIcon.visibility = View.GONE
            binding.textViewPhoneLabel.visibility = View.GONE
        } else {
            binding.textViewPhoneContent.text = property.phoneNumber
        }

        if (property.website.isEmpty()) {
            binding.textViewWebsiteContent.visibility = View.GONE
            binding.cardViewWebsite.visibility = View.GONE
            binding.websiteIcon.visibility = View.GONE
            binding.textViewWebsiteLabel.visibility = View.GONE
        } else {
            binding.textViewWebsiteContent.text = property.website
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}