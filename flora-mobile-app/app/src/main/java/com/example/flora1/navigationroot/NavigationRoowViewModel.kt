package com.example.flora1.navigationroot

import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationRootViewModel @Inject constructor(
    preferences: Preferences,
) : ViewModel() {

}
