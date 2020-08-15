package com.mygdx.game.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetHandler {
    public final AssetManager manager = new AssetManager();
    public final FileHandleResolver resolver = new InternalFileHandleResolver();

    public final AssetDescriptor<Texture> wallpaper = new AssetDescriptor<Texture>("wallpaper.png",Texture.class);
    public final AssetDescriptor<Texture> menuwallpaper = new AssetDescriptor<Texture>("menuwallpaper.png",Texture.class);
    public final AssetDescriptor<Texture> choices = new AssetDescriptor<Texture>("choices/choices.png",Texture.class);
    public final AssetDescriptor<Skin> skin = new AssetDescriptor<Skin>("skins/glassy/skin/glassy-ui.json",Skin.class);
    public final AssetDescriptor<Texture> btnUp = new AssetDescriptor<Texture>("buttons/up.png",Texture.class);
    public final AssetDescriptor<Texture> btnOver = new AssetDescriptor<Texture>("buttons/over.png",Texture.class);
    public final AssetDescriptor<Texture> btnDown = new AssetDescriptor<Texture>("buttons/down.png",Texture.class);
    public final AssetDescriptor<Texture> btnLock = new AssetDescriptor<Texture>("buttons/locked.png",Texture.class);
    public final AssetDescriptor<Texture> btnHome = new AssetDescriptor<Texture>("buttons/home.png",Texture.class);
    public final String fontBangers = "fonts/Bangers-Regular.ttf";

    public void load() {
        loadFont();
        manager.load(wallpaper);
        manager.load(menuwallpaper);
        manager.load(choices);
        manager.load(skin);
        manager.load(btnUp);
        manager.load(btnOver);
        manager.load(btnDown);
        manager.load(btnLock);
        manager.load(btnHome);
    }

    private void loadFont() {
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = fontBangers;
        fontParameter.fontParameters.size = 75;

        fontParameter.fontParameters.minFilter = Texture.TextureFilter.Linear;
        fontParameter.fontParameters.magFilter = Texture.TextureFilter.Linear;

        manager.load(fontBangers, BitmapFont.class, fontParameter);
    }

    public void dispose() {
        manager.dispose();
    }

}
