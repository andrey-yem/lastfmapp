package com.example.andrey.lastfmapp.di;

import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsActivity;

import dagger.Component;

@Component(
        dependencies = ApplicationComponent.class,
        modules = AlbumDetailsModule.class
)
@ActivityScope
public interface AlbumDetailsComponent {

    void inject(AlbumDetailsActivity activity);

}
