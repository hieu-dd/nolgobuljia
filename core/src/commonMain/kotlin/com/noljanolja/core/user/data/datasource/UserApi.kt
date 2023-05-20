package com.noljanolja.core.user.data.datasource

import com.noljanolja.core.base.ResponseWithoutData
import com.noljanolja.core.user.data.model.request.PushTokensRequest
import com.noljanolja.core.user.data.model.request.SyncUserContactsRequest
import com.noljanolja.core.user.data.model.request.UpdateUserRequest
import com.noljanolja.core.user.data.model.response.GetMeResponse
import com.noljanolja.core.user.data.model.response.GetUsersResponse
import com.noljanolja.core.user.data.model.response.UpdateUserResponse
import com.noljanolja.core.utils.Const.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserApi(private val client: HttpClient) {

    suspend fun getMe(): GetMeResponse {
        return client.get("$BASE_URL/users/me").body()
    }

    suspend fun pushTokens(pushTokensRequest: PushTokensRequest): ResponseWithoutData {
        return client.post("$BASE_URL/push-tokens") {
            setBody(pushTokensRequest)
        }.body()
    }

    suspend fun syncUserContacts(request: SyncUserContactsRequest): GetUsersResponse {
        return client.post("$BASE_URL/users/me/contacts") {
            setBody(request)
        }.body()
    }

    suspend fun updateUser(request: UpdateUserRequest): UpdateUserResponse {
        return client.put("$BASE_URL/users/me") {
            setBody(request)
        }.body()
    }

    suspend fun getContacts(page: Int): GetUsersResponse {
        return client.get("$BASE_URL/users/me/contacts?page=$page").body()
    }

    suspend fun findContacts(text: String): GetUsersResponse {
        return client.get("$BASE_URL/users?phoneNumber=$text").body()
    }
}