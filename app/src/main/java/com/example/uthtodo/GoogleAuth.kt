package com.example.uthtodo

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

suspend fun getGoogleToken(context: Context, webClientId: String): String {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()

    val result = CredentialManager.create(context).getCredential(context, request)
    return GoogleIdTokenCredential.createFrom(result.credential.data).idToken
}