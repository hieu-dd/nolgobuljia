package com.noljanolja.core.loyalty.data.datasource

import com.noljanolja.core.loyalty.data.model.request.GetLoyaltyPointsRequest
import com.noljanolja.core.loyalty.data.model.response.GetLoyaltyPointsResponse
import com.noljanolja.core.loyalty.data.model.response.GetMemberInfoResponse
import com.noljanolja.core.utils.Const
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class LoyaltyApi(
    private val client: HttpClient,
) {
    suspend fun getMemberInfo(): GetMemberInfoResponse {
        return client.get("${Const.BASE_URL}/loyalty/me").body()
    }

    suspend fun getLoyaltyPoints(request: GetLoyaltyPointsRequest): GetLoyaltyPointsResponse {
        return client.get("${Const.BASE_URL}/loyalty/me/points") {
            url {
                with(request) {
                    parameters.append("type", type.name)
                    month?.let { parameters.append("month", month.toString()) }
                    year?.let { parameters.append("year", year.toString()) }
                }
            }
        }.body()
    }
}