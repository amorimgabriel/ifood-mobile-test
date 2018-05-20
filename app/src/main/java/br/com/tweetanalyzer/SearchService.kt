package br.com.tweetanalyzer

import android.app.IntentService
import android.content.Intent
import br.com.tweetanalyzer.GNaturalApi.NLanguageSearch
import br.com.tweetanalyzer.events.TokenRetrieveEvent
import br.com.tweetanalyzer.events.TwetterListResult
import br.com.tweetanalyzer.models.Document
import br.com.tweetanalyzer.models.SentimentalAnalyseResult
import br.com.tweetanalyzer.twitterApi.ApiSearchService
import br.com.tweetanalyzer.twitterApi.TwitterConstants
import br.com.tweetanalyzer.util.Constant
import br.com.tweetanalyzer.util.JobType
import br.com.tweetanalyzer.util.Util
import org.greenrobot.eventbus.EventBus

/**
 * Created by gabrielsamorim
 * on 15/05/18.
 */
class SearchService : IntentService("RequestTwitterList") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val jobType = JobType.valueOf(intent.getIntExtra(Constant.JOB_TYPE, 0).toString())
            when (jobType) {
                JobType.GET_AUTH -> getAuth()
                JobType.GET_USER_INFO -> getUserInfo(intent.getStringExtra(Constant.SEARCH_USER_INPUT))
                JobType.SEARCH_INPUT -> searchTwitter(intent.getStringExtra(Constant.SEARCH_INPUT))
                JobType.ANALYSE_SENTIMENT -> analyseSentiment(intent.getStringExtra(Constant.ANALYSE_SENTIMENT))
                JobType.UNKNOWN -> {/*do nothing */}
            }
        }
    }

    private fun getAuth() {
        val token = ApiSearchService().getAuth("Basic " + Util.getBase64(TwitterConstants.TOKEN_TWITTER), "client_credentials")
        if (token != null) {
            PreferenceController.setToken(this, token.token)
            PreferenceController.setTokenType(this, token.tokenType)

            EventBus.getDefault().post(TokenRetrieveEvent(true))
        } else
            EventBus.getDefault().post(TokenRetrieveEvent(false))
    }

    private fun getUserInfo(userName: String) =
            EventBus.getDefault().post(ApiSearchService().getTwitterUser("Bearer " + PreferenceController.getToken(baseContext), userName))

    private fun searchTwitter(twitterUser: String) =
            EventBus.getDefault().post(TwetterListResult(ApiSearchService().getTwitterList("Bearer " + PreferenceController.getToken(baseContext), twitterUser)))

    private fun analyseSentiment(text: String) {
        val result: SentimentalAnalyseResult? = NLanguageSearch().analyseText(Document(text))
    }

}
