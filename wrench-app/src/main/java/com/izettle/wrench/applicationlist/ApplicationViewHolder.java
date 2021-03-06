package com.izettle.wrench.applicationlist;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.izettle.wrench.R;
import com.izettle.wrench.database.WrenchApplication;
import com.izettle.wrench.databinding.ApplicationListItemBinding;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

class ApplicationViewHolder extends RecyclerView.ViewHolder {
    final ApplicationListItemBinding binding;

    ApplicationViewHolder(ApplicationListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bindTo(WrenchApplication application) {
        try {
            PackageManager packageManager = binding.getRoot().getContext().getPackageManager();
            Drawable icon = packageManager.getApplicationIcon(application.packageName());
            binding.applicationIcon.setImageDrawable(icon);
            binding.status.setText("");

        } catch (PackageManager.NameNotFoundException e) {
            binding.applicationIcon.setImageResource(R.drawable.ic_report_black_24dp);
            binding.status.setText(R.string.not_installed);
            e.printStackTrace();
        }
        binding.applicationName.setText(application.applicationLabel());

        binding.getRoot().setOnClickListener(v -> {
            long applicationId = application.id();
            Navigation.findNavController(v).navigate(ApplicationsFragmentDirections.actionApplicationsFragmentToConfigurationsFragment((int) applicationId));
        });
    }

    void clear() {
        binding.applicationIcon.setImageDrawable(null);
        binding.applicationName.setText(null);
        binding.status.setText(null);
        binding.getRoot().setOnClickListener(null);
    }
}
