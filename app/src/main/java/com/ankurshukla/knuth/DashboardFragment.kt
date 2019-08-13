package com.ankurshukla.knuth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    lateinit var holderActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        holderActivity = activity as MainActivity
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Picasso.get().load(holderActivity.mAuth.currentUser?.photoUrl).into(activity?.findViewById<ImageView>(R.id.user_profile_photo))
        Log.d("user image url", holderActivity.mAuth.currentUser?.photoUrl.toString())
        user_profile_photo.invalidate()
        username.text = holderActivity.mAuth.currentUser?.displayName
        logout_button.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        holderActivity.mGoogleSignInClient.signOut().addOnCompleteListener {
            holderActivity.finish()
        }
    }

}
