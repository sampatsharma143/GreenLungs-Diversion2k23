package com.shunyank.appwriteproject.activities.ui.notifications;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.shunyank.appwriteproject.App;
import com.shunyank.appwriteproject.activities.EditProfileActivity;
import com.shunyank.appwriteproject.databinding.FragmentProfileBinding;
import com.shunyank.appwriteproject.models.CompleteUserModel;
import com.shunyank.appwriteproject.network.AppWriteHelper;
import com.shunyank.appwriteproject.network.Constants;
import com.shunyank.appwriteproject.network.callbacks.DocumentFetchListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.utils.Pref;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Account;
import io.appwrite.services.Avatars;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                io.appwrite.services.Account account = new Account(AppWriteHelper.getClient());
                try {
                    account.deleteSessions(new Continuation<Object>() {
                        @Override
                        public void resumeWith(@NonNull Object o) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new CountDownTimer(1500,1000){

                                        @Override
                                        public void onTick(long l) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            binding.progressBar.setVisibility(View.GONE);
                                            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                                            Pref.saveBasicUser(null);
                                            requireActivity().finish();
                                        }
                                    }.start();

                                }
                            });


                        }

                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }
                    });
                } catch (AppwriteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        setProfile();
        fetchUserDetails();
        return root;
    }

    private void fetchUserDetails() {
        DatabaseUtils.fetchDocument(Constants.UserCollectionId, Pref.getBasicUser().getId(), new DocumentFetchListener() {
            @Override
            public void onFetchSuccessfully(Document document) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CompleteUserModel model = DatabaseUtils.convertToModel(document,CompleteUserModel.class);
                        binding.userEmail.setText(model.getEmail());
                        binding.userName.setText(model.getName());
                        binding.ageTxt.setText(model.getAge());
                        binding.orgNameTxt.setText(model.getOrganisationName());
                        binding.phoneTxt.setText(model.getContactNumber());
                        binding.userLevel.setText(model.getLevel());
//                        Log.e("points", String.valueOf(model.getPoints()));
                        binding.userPointText.setText(model.getPoints()+" leaves");
                    }
                });

            }

            @Override
            public void onFailed(Result.Failure failure) {

            }

            @Override
            public void onException(AppwriteException exception) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        Avatars avatars = new Avatars(AppWriteHelper.getClient());

        try {
            avatars.getInitials(new Continuation<byte[]>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {
                  getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(o instanceof Result.Failure){
                                Toast.makeText(requireActivity(), "Unable to load profile", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                            }else {

                                byte[] bytes = (byte[]) o;
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
//                            binding.profileImage.setImageBitmap(bitmap);

                                binding.profileImage.setVisibility(View.VISIBLE);

                                Glide.with(binding.getRoot().getContext()).load(bytes).into(binding.profileImage);
                                binding.progressBar.setVisibility(View.GONE);
                            }

                        }
                    });

                }
            });
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }

        // other details



    }

}