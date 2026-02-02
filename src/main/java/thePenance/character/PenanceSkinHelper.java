package thePenance.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import java.util.ArrayList;
import thePenance.PenanceMod;

import com.esotericsoftware.spine38.AnimationState;
import com.esotericsoftware.spine38.AnimationStateData;
import com.esotericsoftware.spine38.Skeleton;
import com.esotericsoftware.spine38.SkeletonData;
import com.esotericsoftware.spine38.SkeletonJson;
import com.esotericsoftware.spine38.SkeletonRenderer;

public class PenanceSkinHelper {

    // --- 数据结构 ---
    public static class SkinInfo {
        public String name;
        public String atlas;
        public String json;
        public float scale;

        public SkinInfo(String name, String atlas, String json, float scale) {
            this.name = name;
            this.atlas = atlas;
            this.json = json;
            this.scale = scale;
        }
    }

    // --- 皮肤列表 ---
    public static final SkinInfo[] SKINS = new SkinInfo[] {
            new SkinInfo(
                    "默认",
                    "thePenance/char/penance/animation/1/char_4065_judge.atlas",
                    "thePenance/char/penance/animation/1/char_4065_judge.json",
                    1.5f
            ),
            new SkinInfo(
                    "偶尔醉陶",
                    "thePenance/char/penance/animation/2/char_4065_judge_snow_6.atlas",
                    "thePenance/char/penance/animation/2/char_4065_judge_snow_6.json",
                    1.5f
            ),
            new SkinInfo(
                    "记叙",
                    "thePenance/char/penance/animation/3/char_4065_judge_epoque_33.atlas",
                    "thePenance/char/penance/animation/3/char_4065_judge_epoque_33.json",
                    1.5f
            )
    };

    public static int currentSkinIndex = 0;

    // --- 资源字段 ---
    private static TextureAtlas currentAtlas; // 持有引用以便释放
    private static Skeleton previewSkeleton;
    private static AnimationState previewState;
    private static AnimationStateData previewStateData;
    private static SkeletonRenderer skeletonRenderer; // 修正类型

    // --- UI 控件 ---
    private static Hitbox skinLeftHb = new Hitbox(70f * Settings.scale, 70f * Settings.scale);
    private static Hitbox skinRightHb = new Hitbox(70f * Settings.scale, 70f * Settings.scale);

    public static SkinInfo getCurrentSkin() {
        return SKINS[currentSkinIndex];
    }

    public static boolean isPenanceSelected() {
        if (CardCrawlGame.mainMenuScreen == null || CardCrawlGame.mainMenuScreen.charSelectScreen == null) {
            return false;
        }
        ArrayList<CharacterOption> options = ReflectionHacks.getPrivate(
                CardCrawlGame.mainMenuScreen.charSelectScreen, CharacterSelectScreen.class, "options");

        for (CharacterOption o : options) {
            Object p = ReflectionHacks.getPrivate(o, CharacterOption.class, "c");
            // 确保 Penance 类被正确引用
            if (p instanceof Penance && o.selected) {
                return true;
            }
        }
        return false;
    }

    public static void loadPreviewAnimation() {
        SkinInfo skin = getCurrentSkin();

        // 释放旧资源，防止显存泄漏
        if (currentAtlas != null) {
            currentAtlas.dispose();
        }

        currentAtlas = new TextureAtlas(Gdx.files.internal(skin.atlas));
        SkeletonJson json = new SkeletonJson(currentAtlas); // 使用 3.8 的 Loader
        json.setScale(Settings.renderScale / skin.scale); // 计算缩放

        SkeletonData data = json.readSkeletonData(Gdx.files.internal(skin.json));

        previewSkeleton = new Skeleton(data);
        previewSkeleton.setColor(Color.WHITE);
        previewStateData = new AnimationStateData(data);
        previewState = new AnimationState(previewStateData);
        previewState.setAnimation(0, "Idle", true);

        // 设置预览位置
        previewSkeleton.setPosition(Settings.WIDTH * 0.85f, Settings.HEIGHT * 0.65f);

        if (skeletonRenderer == null) {
            skeletonRenderer = new SkeletonRenderer(); // 修正实例化
            skeletonRenderer.setPremultipliedAlpha(true);
        }
    }

    public static void saveSkin() {
        try {
            if (PenanceMod.penanceConfig != null) {
                PenanceMod.penanceConfig.setInt("skinIndex", currentSkinIndex);
                PenanceMod.penanceConfig.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update() {
        if (!isPenanceSelected()) return;

        float centerX = Settings.WIDTH * 0.85f;
        float centerY = Settings.HEIGHT * 0.65f;

        skinLeftHb.move(centerX - 120f * Settings.scale, centerY);
        skinRightHb.move(centerX + 120f * Settings.scale, centerY);
        skinLeftHb.update();
        skinRightHb.update();

        if (InputHelper.justClickedLeft) {
            if (skinLeftHb.hovered || skinRightHb.hovered) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                if (skinLeftHb.hovered) skinLeftHb.clickStarted = true;
                if (skinRightHb.hovered) skinRightHb.clickStarted = true;
            }
        }

        if (skinLeftHb.clicked || skinRightHb.clicked) {
            if (skinLeftHb.clicked) {
                currentSkinIndex--;
                if (currentSkinIndex < 0) currentSkinIndex = SKINS.length - 1;
            } else {
                currentSkinIndex++;
                if (currentSkinIndex >= SKINS.length) currentSkinIndex = 0;
            }
            skinLeftHb.clicked = false;
            skinRightHb.clicked = false;

            saveSkin();

            loadPreviewAnimation();
        }
    }

    public static void render(SpriteBatch sb) {
        if (!isPenanceSelected()) return;
        if (previewSkeleton == null) loadPreviewAnimation();

        float centerX = Settings.WIDTH * 0.85f;
        float centerY = Settings.HEIGHT * 0.65f;

        // 1. 绘制箭头
        sb.setColor(skinLeftHb.hovered ? Color.LIGHT_GRAY : Color.WHITE);
        sb.draw(ImageMaster.CF_LEFT_ARROW, skinLeftHb.cX - 24f, skinLeftHb.cY - 24f, 24f, 24f, 48f, 48f, Settings.scale, Settings.scale, 0f, 0, 0, 48, 48, false, false);

        sb.setColor(skinRightHb.hovered ? Color.LIGHT_GRAY : Color.WHITE);
        sb.draw(ImageMaster.CF_RIGHT_ARROW, skinRightHb.cX - 24f, skinRightHb.cY - 24f, 24f, 24f, 48f, 48f, Settings.scale, Settings.scale, 0f, 0, 0, 48, 48, false, false);

        // 2. 绘制文字
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, getCurrentSkin().name, centerX, centerY, Settings.GOLD_COLOR);

        // 3. 绘制 Spine 预览
        // 更新逻辑
        float deltaTime = Gdx.graphics.getDeltaTime();
        previewState.update(deltaTime);
        previewState.apply(previewSkeleton);
        previewSkeleton.updateWorldTransform();
        previewSkeleton.setPosition(centerX, centerY);

        // 绘制逻辑 (切换 Batch)
        sb.end();
        CardCrawlGame.psb.begin();
        skeletonRenderer.draw(CardCrawlGame.psb, previewSkeleton);
        CardCrawlGame.psb.end();
        sb.begin();
    }
}