package com.neoapps.skijahorina.common

import android.content.Context
import android.view.View
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.databinding.LayoutUnableToFetchDataBinding

enum class FetchEmptyKind {
    NO_INTERNET,
    STALE_CACHE,
    SERVER_WARMING_UP,
}

object FetchEmptyState {

    fun resolve(context: Context, hasCachedData: Boolean): FetchEmptyKind {
        val online = NetworkStatus.isOnline(context)
        return when {
            !online -> FetchEmptyKind.NO_INTERNET
            hasCachedData -> FetchEmptyKind.STALE_CACHE
            else -> FetchEmptyKind.SERVER_WARMING_UP
        }
    }

    fun bind(
        binding: LayoutUnableToFetchDataBinding,
        kind: FetchEmptyKind,
        onRetry: () -> Unit,
    ) {
        binding.noInternet.visibility = View.VISIBLE
        val context = binding.root.context
        when (kind) {
            FetchEmptyKind.NO_INTERNET -> {
                binding.textView.text = context.getString(R.string.empty_no_internet_headline)
                binding.textView1.text = context.getString(R.string.empty_no_internet_text)
            }
            FetchEmptyKind.STALE_CACHE -> {
                binding.textView.text = context.getString(R.string.empty_stale_headline)
                binding.textView1.text = context.getString(R.string.empty_stale_text)
            }
            FetchEmptyKind.SERVER_WARMING_UP -> {
                binding.textView.text = context.getString(R.string.empty_warming_headline)
                binding.textView1.text = context.getString(R.string.empty_warming_text)
            }
        }
        binding.retryButton.visibility = View.VISIBLE
        binding.retryButton.setOnClickListener { onRetry() }
    }

    fun hide(binding: LayoutUnableToFetchDataBinding) {
        binding.noInternet.visibility = View.GONE
        binding.retryButton.setOnClickListener(null)
    }
}
