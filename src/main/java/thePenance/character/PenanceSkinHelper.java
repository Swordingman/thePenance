package thePenance.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonMeshRenderer;
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

    // --- 皮肤列表与状态 ---
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
            )
    };

    public static int currentSkinIndex = 0;

    // --- 预览渲染资源 ---
    private static Skeleton previewSkeleton;
    private static AnimationState previewState;
    private static AnimationStateData previewStateData;
    private static SkeletonMeshRenderer skeletonRenderer;

    // --- UI 控件 ---
    private static Hitbox skinLeftHb = new Hitbox(70f * Settings.scale, 70f * Settings.scale);
    private static Hitbox skinRightHb = new Hitbox(70f * Settings.scale, 70f * Settings.scale);

    /** 获取当前选中的皮肤信息 */
    public static SkinInfo getCurrentSkin() {
        return SKINS[currentSkinIndex];
    }

    /** 检查是否在选人界面且选中了斥罪 */
    public static boolean isPenanceSelected() {
        if (CardCrawlGame.mainMenuScreen == null || CardCrawlGame.mainMenuScreen.charSelectScreen == null) {
            return false;
        }
        ArrayList<CharacterOption> options = ReflectionHacks.getPrivate(
                CardCrawlGame.mainMenuScreen.charSelectScreen, CharacterSelectScreen.class, "options");

        for (CharacterOption o : options) {
            Object p = ReflectionHacks.getPrivate(o, CharacterOption.class, "c");
            if (p instanceof Penance && o.selected) { // 注意这里要引用 Penance 类
                return true;
            }
        }
        return false;
    }

    /** 加载预览用的 Spine 资源 */
    public static void loadPreviewAnimation() {
        SkinInfo skin = getCurrentSkin();

        // 资源加载
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(skin.atlas));
        SkeletonJson json = new SkeletonJson(atlas);
        json.setScale(Settings.renderScale / skin.scale);
        SkeletonData data = json.readSkeletonData(Gdx.files.internal(skin.json));

        // 状态初始化
        previewSkeleton = new Skeleton(data);
        previewSkeleton.setColor(Color.WHITE);
        previewStateData = new AnimationStateData(data);
        previewState = new AnimationState(previewStateData);
        previewState.setAnimation(0, "Idle", true);

        if (skeletonRenderer == null) {
            skeletonRenderer = new SkeletonMeshRenderer();
            skeletonRenderer.setPremultipliedAlpha(true);
        }
    }

    /** 处理点击和更新逻辑 */
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

            loadPreviewAnimation(); // 重新加载预览
        }
    }

    /** 渲染逻辑 */
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
        float deltaTime = Gdx.graphics.getDeltaTime();
        previewState.update(deltaTime);
        previewState.apply(previewSkeleton);
        previewSkeleton.updateWorldTransform();
        previewSkeleton.setPosition(centerX, centerY);

        sb.end();
        CardCrawlGame.psb.begin();
        skeletonRenderer.draw(CardCrawlGame.psb, previewSkeleton);
        CardCrawlGame.psb.end();
        sb.begin();
    }
}