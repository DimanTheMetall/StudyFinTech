package test.repositries

import RxRule
import android.annotation.SuppressLint
import com.example.homework2.dataclasses.chatdataclasses.Website
import com.example.homework2.dataclasses.streamsandtopics.JsonUsers
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.repositories.UsersRepositoryImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Single
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock


class UsersRepositoryImplTest() {

    @get:Rule
    val rxRule = RxRule()


    private val retrofitService: RetrofitService = mock(RetrofitService::class.java)

    private val usersRepository = UsersRepositoryImpl(retrofitService)

    @SuppressLint("CheckResult")
    @Test
    fun `test loadAllUsersWithOutPresence `() {
        val member = createMember(
            avatarURL = "avatar",
            email = "email",
            fullName = "fullName",
            isActive = true,
            isOwner = true,
            role = 20,
            userid = 25,
            msg = "msg",
            Website(status = "status", timestamp = 30)
        )
        val memberList = createMembersList(member = member)
        val jsonUsers = JsonUsers(members = memberList, result = "result")
        Mockito.`when`(retrofitService.getUsers()).thenReturn(Single.just(jsonUsers))

        var members: List<Member>? = null
        usersRepository.loadAllUsersWithOutPresence().subscribe({
            members = it
        }, {})

        Assert.assertEquals(members, memberList)
    }

    private fun createMember(
        avatarURL: String? = "",
        email: String = "",
        fullName: String = "",
        isActive: Boolean = false,
        isOwner: Boolean = false,
        role: Int = -1,
        userid: Int = -1,
        msg: String = "",
        website: Website = Website("", -1),
    ): Member {
        return Member(
            avatarUrl = avatarURL,
            email = email,
            fullName = fullName,
            isActive = isActive,
            isOwner = isOwner,
            role = role,
            userId = userid,
            msg = msg,
            website = website
        )
    }

    private fun createJsonUsers(
        member: Member,
        result: String = ""
    ): JsonUsers {
        return JsonUsers(
            createMembersList(
                member
            ), result
        )
    }

    private fun createMembersList(
        member: Member
    ): List<Member> {
        return listOf(
            member, member, member
        )
    }

}