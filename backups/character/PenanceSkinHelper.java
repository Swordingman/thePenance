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

// --- Spine 3.8 引用 ---
import com.esotericsoftware.spine38.AnimationState;
import com.esotericsoftware.spine38.AnimationStateData;
import com.esotericsoftware.spine38.Skeleton;
import com.esotericsoftware.spine38.SkeletonBinary;
import com.esotericsoftware.spine38.SkeletonData;
import com.esotericsoftware.spine38.SkeletonMeshRenderer;

import java.util.ArrayList;

public class PenanceSkinHelper {

    // --- 数据结构 ---
    public static class SkinInfo {
        public String name;        // UI显示的名称
        public String spineSkinId; // Spine内部ID (通常是 default)
        public String atlas;       // Atlas 路径
        public String skel;        // Skel 路径 (二进制)
        public float scale;

        public SkinInfo(String name, String spineSkinId, String atlas, String skel, float scale) {
            this.name = name;
            this.spineSkinId = spineSkinId;
            this.atlas = atlas;
            this.skel = skel;
            this.scale = scale;
        }
    }

    // --- 皮肤列表 (按你的要求配置) ---
    public static final SkinInfo[] SKINS = new SkinInfo[] {
            new SkinInfo(
                    "默认",
                    "default",
                    "thePenance/char/penance/animation/1 - new/char_4065_judge.atlas",
                    "thePenance/char/penance/animation/1 - new/char_4065_judge.skel",
                    1.5f
            ),
            new SkinInfo(
                    "偶尔醉陶",
                    "default",
                    "thePenance/char/penance/animation/2 - new/char_4065_judge_snow_6.atlas",
                    "thePenance/char/penance/animation/2 - new/char_4065_judge_snow_6.skel",
                    1.5f
            )
    };

    public static int currentSkinIndex = 0;

    // --- 预览渲染资源 ---
    private static Skeleton previewSkeleton;
    private static AnimationState previewState;
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
            if (p instanceof Penance && o.selected) {
                return true;
            }
        }
        return false;
    }

    /** 加载预览用的 Spine 3.8 资源 (使用 Binary) */
    public static void loadPreviewAnimation() {
        SkinInfo skin = getCurrentSkin();

        // 1. 加载 Atlas
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(skin.atlas));

        // 2. 使用 SkeletonBinary 读取 .skel
        SkeletonBinary binary = new SkeletonBinary(atlas);
        binary.setScale(Settings.renderScale / skin.scale);
        SkeletonData data = binary.readSkeletonData(Gdx.files.internal(skin.skel));

        // 3. 初始化对象
        previewSkeleton = new Skeleton(data);
        previewSkeleton.setColor(Color.WHITE);

        // 设置皮肤 (带保底)
        try {
            previewSkeleton.setSkin(skin.spineSkinId);
        } catch (Exception e) {
            previewSkeleton.setSkin("default");
        }

        AnimationStateData stateData = new AnimationStateData(data);
        previewState = new AnimationState(stateData);
        previewState.setAnimation(0, "Idle", true);

        // 4. 初始化渲染器
        if (skeletonRenderer == null) {
            skeletonRenderer = new SkeletonMeshRenderer();
            skeletonRenderer.setPremultipliedAlpha(true);
        }
    }

    /** 更新逻辑 */
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

            loadPreviewAnimation(); // 切换皮肤后重新加载
        }
    }

    /** 渲染逻辑 */
    public static void render(SpriteBatch sb) {
        if (!isPenanceSelected()) return;
        if (previewSkeleton == null) loadPreviewAnimation();

        float centerX = Settings.WIDTH * 0.85f;
        float centerY = Settings.HEIGHT * 0.65f;

        // 绘制箭头
        sb.setColor(skinLeftHb.hovered ? Color.LIGHT_GRAY : Color.WHITE);
        sb.draw(ImageMaster.CF_LEFT_ARROW, skinLeftHb.cX - 24f, skinLeftHb.cY - 24f, 24f, 24f, 48f, 48f, Settings.scale, Settings.scale, 0f, 0, 0, 48, 48, false, false);
        sb.setColor(skinRightHb.hovered ? Color.LIGHT_GRAY : Color.WHITE);
        sb.draw(ImageMaster.CF_RIGHT_ARROW, skinRightHb.cX - 24f, skinRightHb.cY - 24f, 24f, 24f, 48f, 48f, Settings.scale, Settings.scale, 0f, 0, 0, 48, 48, false, false);

        // 绘制文字
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, getCurrentSkin().name, centerX, centerY, Settings.GOLD_COLOR);

        // 绘制 Spine 3.8 预览
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