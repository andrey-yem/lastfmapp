package com.example.andrey.lastfmapp.di;

import com.example.andrey.lastfmapp.ui.album_list.AlbumListActivity;

import dagger.Component;

@Component(
        dependencies = ApplicationComponent.class,
        modules = AlbumListModule.class
)
@ActivityScope
public interface AlbumListComponent {

    void inject(AlbumListActivity activity);

}
