package com.neoapps.skijahorina.features.skicenter.properties

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.databinding.FragmentPropertyDetailsBinding
import com.neoapps.skijahorina.main.MainActivity

class PropertyDetailsFragment : Fragment() {

    private var _binding: FragmentPropertyDetailsBinding? = null
    private val binding get() = _binding!!

    private val propertyViewModel: PropertyViewModel by viewModels()

    private lateinit var storageRef: StorageReference
    private var propertyFirst: Property? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)
        setUpFragmentName()
        AppAnalytics.logFeatureOpened(AppAnalytics.Feature.APARTMENTS)

        storageRef = FirebaseStorage.getInstance("gs://ski-jahorina.firebasestorage.app")
            .reference
            .child("properties")

        binding.cardViewEmail.setOnClickListener { openEmail() }
        binding.cardViewPhoneNumber.setOnClickListener { openPhone() }
        binding.cardViewWebsite.setOnClickListener { openWebsite() }
        binding.textViewExpandDescription.setOnClickListener { toggleDescriptionExpanded() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyViewModel.properties.observe(viewLifecycleOwner) { properties ->
            if (!isViewActive()) return@observe
            if (properties.isEmpty()) return@observe

            propertyFirst = properties.first()
            val property = propertyFirst ?: return@observe

            binding.textViewApartmentName.text = property.name
            binding.textViewLocation.text = property.location
            setupExpandableDescription(property.description)
            loadImagesFromStorage(property.pictures)
            setTheVisibilityOfTheBasicContacts(property)
        }

        propertyViewModel.loadProperties()
    }

    private fun openEmail() {
        val property = propertyFirst
        val recipientEmail = if (property != null && property.email.isNotBlank()) {
            property.email
        } else {
            getString(R.string.default_email_property)
        }
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipientEmail")
            putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.default_subject))
        }
        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            showSnackbar(resources.getString(R.string.property_email_app_validation))
        }
    }

    private fun openPhone() {
        val property = propertyFirst
        val phoneNumber = if (property != null && property.phoneNumber.isNotBlank()) {
            property.phoneNumber
        } else {
            getString(R.string.default_number_property)
        }
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
    }

    private fun openWebsite() {
        val property = propertyFirst
        val website = if (property != null && property.website.isNotBlank()) {
            property.website
        } else {
            getString(R.string.default_website_property)
        }
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website)))
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
                if (!isViewActive()) return@addOnSuccessListener
                val itemsByName = listResult.items.associateBy { it.name.substringBeforeLast(".") }

                requiredImageNames.forEachIndexed { index, imageName ->
                    val (imageView, cardView) = imageViewMap[index]
                    val item = itemsByName[imageName]

                    if (item != null) {
                        item.downloadUrl.addOnSuccessListener { uri ->
                            if (!isViewActive()) return@addOnSuccessListener
                            Log.d(TAG, "Uri : $uri")
                            Glide.with(imageView).load(uri).into(imageView)
                            imageUris[index] = uri.toString()
                            cardView.visibility = View.VISIBLE
                            imageView.setOnClickListener {
                                navigateToFullScreenFragment(imageUris.filterNotNull(), index)
                            }
                        }.addOnFailureListener {
                            if (!isViewActive()) return@addOnFailureListener
                            cardView.visibility = View.GONE
                        }
                    } else {
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
                    binding.imageViewPic4.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.propertyImage4Shadowed)
                    )
                } else {
                    binding.textViewForImageView4.visibility = View.GONE
                    binding.cardViewApartmentPic4.visibility = View.GONE
                    binding.imageViewPic4.clearColorFilter()
                }

                extraImageNames.forEachIndexed { index, imageName ->
                    val item = itemsByName[imageName]
                    if (item != null) {
                        item.downloadUrl.addOnSuccessListener { uri ->
                            if (!isViewActive()) return@addOnSuccessListener
                            imageUris[maxImagesToShow + index] = uri.toString()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to list files in storage")
            }
    }

    private fun navigateToFullScreenFragment(imageUrls: List<String>, initialPosition: Int) {
        val action = PropertyDetailsFragmentDirections.actionFullScreenPropertyImageFragment(
            imageUrls.joinToString(","),
            initialPosition
        )
        findNavController().navigate(action)
    }

    private fun isViewActive(): Boolean {
        return _binding != null &&
            viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        val snackbarLayout: View = snackbar.view
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        val textView = snackbarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_about_snackbar, 0, 0, 0)
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.snackbar_warning)
        snackbar.show()
    }

    private var descriptionExpanded = false

    private fun setupExpandableDescription(description: String) {
        val contentView = binding.textViewDescriptionContent
        val toggleView = binding.textViewExpandDescription

        contentView.text = description
        descriptionExpanded = false
        contentView.maxLines = Int.MAX_VALUE

        contentView.post {
            if (!isViewActive()) return@post
            val layout = contentView.layout
            val needsToggle = layout != null && layout.lineCount > DESCRIPTION_COLLAPSED_LINES
            if (needsToggle) {
                contentView.maxLines = DESCRIPTION_COLLAPSED_LINES
                toggleView.visibility = View.VISIBLE
            } else {
                toggleView.visibility = View.GONE
            }
            updateDescriptionToggleLabel()
        }
    }

    private fun toggleDescriptionExpanded() {
        descriptionExpanded = !descriptionExpanded
        binding.textViewDescriptionContent.maxLines =
            if (descriptionExpanded) Int.MAX_VALUE else DESCRIPTION_COLLAPSED_LINES
        updateDescriptionToggleLabel()
    }

    private fun updateDescriptionToggleLabel() {
        binding.textViewExpandDescription.text = getString(
            if (descriptionExpanded) R.string.read_less else R.string.read_more
        )
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        title1TextView?.visibility = View.GONE
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
        _binding?.let { bound ->
            listOf(
                bound.imageViewPic1,
                bound.imageViewPic2,
                bound.imageViewPic3,
                bound.imageViewPic4
            ).forEach { imageView ->
                Glide.with(imageView).clear(imageView)
            }
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "PropertyDetailsFragment"
        private const val DESCRIPTION_COLLAPSED_LINES = 4
    }
}
