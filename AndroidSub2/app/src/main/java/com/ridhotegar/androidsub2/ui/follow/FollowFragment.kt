package com.ridhotegar.androidsub2.ui.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ridhotegar.androidsub2.adapter.ListUserAdapter
import com.ridhotegar.androidsub2.databinding.FragmentFollowBinding
import com.ridhotegar.androidsub2.model.domain.User
import com.ridhotegar.androidsub2.model.domain.UserDetail
import com.ridhotegar.androidsub2.model.response.UserDetailResponse
import com.ridhotegar.androidsub2.model.response.UserItemResponse
import com.ridhotegar.androidsub2.network.ApiConfig
import com.ridhotegar.androidsub2.ui.detail.DetailActivity
import com.ridhotegar.androidsub2.ui.detail.DetailActivity.Companion.KEY_USER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding

    private val listUserAdapter = ListUserAdapter()

    private val listUserFlow = MutableStateFlow<User?>(null)
    private val listUser = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(false)

        showRecyclerList()

        val user = (requireActivity() as DetailActivity).intent.getParcelableExtra<User>(KEY_USER)

        if (user != null) {
            val type = arguments?.getInt(EXTRA_TYPE, 0)

            if (type == 0) {
                getUserFollowersByUsername(user.username)
            } else {
                getUserFollowingByUsername(user.username)
            }
        }

        lifecycleScope.launchWhenCreated {
            listUserFlow.collect {
                if (it != null) {
                    listUser.add(it)
                    listUserAdapter.setData(listUser)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUserFollowersByUsername(username: String?) {
        showLoading(true)

        ApiConfig.userService.getUserFollowersByUsername(username)
            .enqueue(object : Callback<List<UserItemResponse>> {
                override fun onResponse(
                    call: Call<List<UserItemResponse>>,
                    response: Response<List<UserItemResponse>>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        if (users.isNullOrEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Error mendapatkan data",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            listUserAdapter.setData(users.map {
                                User(
                                    id = it.id ?: -1,
                                    name = it.name ?: "-",
                                    username = it.username ?: "-",
                                    photo = it.avatarUrl.orEmpty()
                                )
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<List<UserItemResponse>>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error mendapatkan data", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun getUserFollowingByUsername(username: String?) {
        showLoading(true)

        ApiConfig.userService.getUserFollowingByUsername(username)
            .enqueue(object : Callback<List<UserItemResponse>> {
                override fun onResponse(
                    call: Call<List<UserItemResponse>>,
                    response: Response<List<UserItemResponse>>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        if (users.isNullOrEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Error mendapatkan data",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            for (user in users) {
                                getDetailUserByUsername(user.username)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<UserItemResponse>>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error mendapatkan data", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun showRecyclerList() {
        binding?.rvUsers?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvUsers?.adapter = listUserAdapter
        binding?.rvUsers?.setHasFixedSize(true)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pbLoading?.isVisible = isLoading
    }

    private fun getDetailUserByUsername(username: String?) {
        ApiConfig.userService.getUserDetailByUsername(username)
            .enqueue(object : Callback<UserDetailResponse> {
                override fun onResponse(
                    call: Call<UserDetailResponse>,
                    response: Response<UserDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        val userDetail = UserDetail(
                            id = data?.id ?: -1,
                            name = data?.name ?: "-",
                            username = data?.username ?: "-",
                            photo = data?.avatarUrl ?: "-",
                            location = data?.location ?: "-",
                            company = data?.company ?: "-",
                            publicRepos = data?.publicRepos ?: 0,
                            follower = data?.follower ?: 0,
                            following = data?.following ?: 0,
                        )

                        val user = User(
                            id = userDetail.id,
                            name = userDetail.name,
                            username = userDetail.username,
                            photo = userDetail.photo
                        )

                        listUserFlow.value = user
                    }
                }

                override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {

                }
            })
    }

    companion object {
        private const val EXTRA_TYPE = "EXTRA_TYPE"

        fun getInstance(type: Int): FollowFragment {
            return FollowFragment().apply {
                val bundle = Bundle().apply {
                    putInt(EXTRA_TYPE, type)
                }

                arguments = bundle
            }
        }
    }

}