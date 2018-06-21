package in.co.androidadda.wallpapertrending.Activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import in.co.androidadda.wallpapertrending.R;

/**
 * Created by Anand
 */

public class IntroActivity extends MaterialIntroActivity {

//    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        enableLastSlideAlphaExitTransition(false);

//        databaseReference.child("Websites").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preferencesName), MODE_PRIVATE);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("wallpaperSite1", dataSnapshot.child("Site1").getValue().toString());
//                editor.putString("wallpaperSite2", dataSnapshot.child("Site2").getValue().toString());
//                editor.apply();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.color1)
                .buttonsColor(R.color.translucentBlackColor)
                .image(R.drawable.introimg)
                .title("Wallpaper Trending")
                .description("The best wallpaper downloading app for Android!")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.color2)
                .buttonsColor(R.color.translucentBlackColor)
                .image(R.drawable.img1)
                .title(getResources().getString(R.string.explore))
                .description("More than 50000+ wallpapers curated for your device")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.color3)
                .buttonsColor(R.color.translucentBlackColor)
                .image(R.drawable.img2)
                .title("Search")
                .description("Find the wallpapers that fits your taste")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.color4)
                .buttonsColor(R.color.translucentBlackColor)
                .image(R.drawable.img3)
                .title("Categories")
                .description("Explore the categories for finding precise wallpapers")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.color5)
                .buttonsColor(R.color.translucentBlackColor)
                .image(R.drawable.img4)
                .title("Add to Favorites")
                .description("Loved a wallpaper? Double Tap to Add them to Favorites so you never lose them")
                .build());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.translucentBlackColor)
                        .image(R.drawable.introimg)
                        .title("Ready to Dive in ?")
                        .neededPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Press 'Tick' to continue", Toast.LENGTH_SHORT).show();
                    }
                }, "You are ready to rock!"));

    }

    @Override
    public void onFinish() {
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.preferencesName), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstRun", false);
        editor.apply();
        super.onFinish();
    }
}

