package com.example.frontend_profilservice;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

public class NavigationUtils {
    
    public static void setupNavigation(Activity activity, int activeIndex) {
        // Elements from the included layout
        final CurvedBottomView curvedView = activity.findViewById(R.id.curved_view);
        final View activeCircle = activity.findViewById(R.id.active_circle);
        final ImageView activeIcon = activity.findViewById(R.id.active_icon);
        
        if (curvedView == null) return;

        // Set the active index for the curve drawing
        curvedView.setActiveIndex(activeIndex);
        
        // Navigation Click Listeners
        View homeBtn = activity.findViewById(R.id.nav_home);
        View servicesBtn = activity.findViewById(R.id.nav_services);
        View profileBtn = activity.findViewById(R.id.nav_profile);
        
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                if (activeIndex != 0) {
                    activity.startActivity(new Intent(activity, HomeActivity.class));
                    activity.overridePendingTransition(0, 0); 
                    activity.finish(); // Finish current to prevent stack buildup? Or keep? Usually keep 'Home' at root.
                    // For simple testing:
                }
            });
        }
        
        if (servicesBtn != null) {
            servicesBtn.setOnClickListener(v -> {
                if (activeIndex != 1) {
                    activity.startActivity(new Intent(activity, ServicesActivity.class));
                    activity.overridePendingTransition(0, 0);
                    if (activeIndex != 0) activity.finish(); // Finish if not main
                }
            });
        }
        
        if (profileBtn != null) {
            profileBtn.setOnClickListener(v -> {
                if (activeIndex != 2) {
                    activity.startActivity(new Intent(activity, DashboardActivity.class));
                    activity.overridePendingTransition(0, 0);
                    if (activeIndex != 0) activity.finish();
                }
            });
        }
        
        // UI Layout for active indicator
        // We need to wait for layout to know widths
        curvedView.post(() -> {
            int width = curvedView.getWidth();
            if (width == 0) return;
            
            int itemWidth = width / 3;
            
            // Calculate X position for the circle to be centered on the item
            // Item centers are at: itemWidth/2, itemWidth + itemWidth/2, etc.
            float targetCenter = (itemWidth * activeIndex) + (itemWidth / 2f);
            // Add curvedView.getX() because the view now has a left margin/offset relative to parent
            float targetX = (targetCenter + curvedView.getX()) - (activeCircle.getWidth() / 2f);
            
            activeCircle.setX(targetX);
            
            // Set the icon in the floating circle
            if (activeIndex == 0) activeIcon.setImageResource(R.drawable.ic_nav_home);
            else if (activeIndex == 1) activeIcon.setImageResource(R.drawable.ic_nav_dashboard);
            else if (activeIndex == 2) activeIcon.setImageResource(R.drawable.ic_nav_profile);
        });
        
        // Optional: Hide the static icon strictly if we want to mimic the image where the static icon DISAPPEARS from the bar 
        // and reappears in the circle. The image shows empty space in the bar.
        ImageView staticHome = activity.findViewById(R.id.icon_home);
        ImageView staticServices = activity.findViewById(R.id.icon_services);
        ImageView staticProfile = activity.findViewById(R.id.icon_profile);
        
        if (staticHome != null) staticHome.setVisibility(activeIndex == 0 ? View.INVISIBLE : View.VISIBLE);
        if (staticServices != null) staticServices.setVisibility(activeIndex == 1 ? View.INVISIBLE : View.VISIBLE);
        if (staticProfile != null) staticProfile.setVisibility(activeIndex == 2 ? View.INVISIBLE : View.VISIBLE);
    }
}
