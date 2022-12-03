package com.louisfn.somovie.test.testfixtures.android.repository

import com.louisfn.somovie.domain.model.Session
import com.louisfn.somovie.domain.repository.SessionRepository
import com.louisfn.somovie.test.testfixtures.android.data.domain.FakeSessionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeSessionRepository : SessionRepository {

    private val sessionState = MutableStateFlow(FakeSessionFactory.empty)

    override fun sessionChanges(): Flow<Session> =
        sessionState

    override suspend fun getSession(): Session =
        sessionState.value

    override suspend fun updateSession(transform: suspend (t: Session) -> Session) {
        sessionState.update { transform(it) }
    }

    fun setSession(session: Session) {
        sessionState.value = session
    }
}
