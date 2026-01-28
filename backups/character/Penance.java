package thePenance.character;

import basemod.BaseMod;
import basemod.abstracts.CustomEnergyOrb;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import thePenance.cards.Defend;
import thePenance.cards.Resolute;
import thePenance.cards.Strike;
import thePenance.relics.PenanceBasicRelic;
import thePenance.util.Sounds;

import java.util.ArrayList;

import static thePenance.PenanceMod.characterPath;
import static thePenance.PenanceMod.makeID;

// --- Spine 3.8 引用 ---
import com.esotericsoftware.spine38.AnimationState;
import com.esotericsoftware.spine38.AnimationStateData;
import com.esotericsoftware.spine38.Skeleton;
import com.esotericsoftware.spine38.SkeletonBinary;
import com.esotericsoftware.spine38.SkeletonData;
import com.esotericsoftware.spine38.SkeletonMeshRenderer;

public class Penance extends CustomPlayer {
    // 角色基础数值
    public static final int ENERGY_PER_TURN = 3;
    public static final int MAX_HP = 80;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;

    // 字符串资源
    private static final String ID = makeID("Penance");
    private static String[] getNames() { return CardCrawlGame.languagePack.getCharacterString(ID).NAMES; }
    private static String[] getText() { return CardCrawlGame.languagePack.getCharacterString(ID).TEXT; }

    // 图片资源
    private static final String CHAR_IMAGE = characterPath("penance.png");
    private static final String SHOULDER_1 = characterPath("shoulder.png");
    private static final String SHOULDER_2 = characterPath("shoulder2.png");

    private static final String[] orbTextures = {
            characterPath("energyorb/layer1.png"), characterPath("energyorb/layer2.png"),
            characterPath("energyorb/layer3.png"), characterPath("energyorb/layer4.png"),
            characterPath("energyorb/layer5.png"), characterPath("energyorb/cover.png"),
            characterPath("energyorb/layer1d.png"), characterPath("energyorb/layer2d.png"),
            characterPath("energyorb/layer3d.png"), characterPath("energyorb/layer4d.png"),
            characterPath("energyorb/layer5d.png")
    };
    private static final float[] layerSpeeds = new float[] { -20.0F, 20.0F, -40.0F, 40.0F, 360.0F };

    // --- Spine 3.8 变量 (仿 Goldenglow 结构) ---
    protected TextureAtlas atlas;
    protected Skeleton skeleton;
    public AnimationState state;
    protected AnimationStateData stateData;

    // 静态渲染器 (节省内存)
    public static PolygonSpriteBatch psb = new PolygonSpriteBatch();
    public static SkeletonMeshRenderer sr = new SkeletonMeshRenderer();
    static {
        sr.setPremultipliedAlpha(true); // 全局开启 PMA
    }

    // --- Meta 类 ---
    public static class Meta {
        @SpireEnum public static PlayerClass PENANCE;
        @SpireEnum(name = "PENANCE_COLOR") public static AbstractCard.CardColor CARD_COLOR;
        @SpireEnum(name = "PENANCE_COLOR") @SuppressWarnings("unused") public static CardLibrary.LibraryType LIBRARY_COLOR;

        private static final String CHAR_SELECT_BUTTON = "thePenance/char/select.png";
        private static final String CHAR_SELECT_PORTRAIT = "thePenance/char/penance.png";
        private static final String BG_ATTACK = characterPath("cardback/bg_attack.png");
        private static final String BG_ATTACK_P = characterPath("cardback/bg_attack_p.png");
        private static final String BG_SKILL = characterPath("cardback/bg_skill.png");
        private static final String BG_SKILL_P = characterPath("cardback/bg_skill_p.png");
        private static final String BG_POWER = characterPath("cardback/bg_power.png");
        private static final String BG_POWER_P = characterPath("cardback/bg_power_p.png");
        private static final String ENERGY_ORB = characterPath("cardback/energy_orb.png");
        private static final String ENERGY_ORB_P = characterPath("cardback/energy_orb_p.png");
        private static final String SMALL_ORB = characterPath("cardback/small_orb.png");
        private static final Color cardColor = new Color(71f/255f, 63f/255f, 34f/255f, 1f);

        public static void registerColor() {
            BaseMod.addColor(CARD_COLOR, cardColor, BG_ATTACK, BG_SKILL, BG_POWER, ENERGY_ORB, BG_ATTACK_P, BG_SKILL_P, BG_POWER_P, ENERGY_ORB_P, SMALL_ORB);
        }
        public static void registerCharacter() {
            BaseMod.addCharacter(new Penance(), CHAR_SELECT_BUTTON, CHAR_SELECT_PORTRAIT, Meta.PENANCE);
        }
    }

    // --- 构造函数 ---
    public Penance() {
        super(getNames()[0], Meta.PENANCE,
                new CustomEnergyOrb(orbTextures, characterPath("energyorb/vfx.png"), layerSpeeds),
                null, null);

        // 初始化：第一个参数传入静态图，作为防崩溃保底
        initializeClass(CHAR_IMAGE, SHOULDER_2, SHOULDER_1, null, getLoadout(),
                20.0F, -30.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));

        // 加载 Spine 动画
        refreshSkin();
    }

    /** 刷新皮肤 (仿 Goldenglow) */
    public void refreshSkin() {
        PenanceSkinHelper.SkinInfo activeSkin = PenanceSkinHelper.getCurrentSkin();

        // 调用二进制加载
        loadAnimation(activeSkin.atlas, activeSkin.skel, activeSkin.scale);

        // 设置动画混合
        this.stateData.setMix("Idle", "Attack", 0.1f);
        this.stateData.setMix("Attack", "Idle", 0.1f);
        this.stateData.setMix("Idle", "Die", 0.1f);

        // 播放待机动画
        this.state.setAnimation(0, "Idle", true);
    }

    /** 核心加载逻辑 (SkeletonBinary) */
    protected void loadAnimation(String atlasUrl, String skelUrl, float scale) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));

        SkeletonBinary binary = new SkeletonBinary(this.atlas);
        binary.setScale(scale * Settings.scale);

        SkeletonData data = binary.readSkeletonData(Gdx.files.internal(skelUrl));

        this.skeleton = new Skeleton(data);
        this.skeleton.setColor(Color.WHITE);

        try {
            this.skeleton.setSkin(PenanceSkinHelper.getCurrentSkin().spineSkinId);
        } catch (Exception e) {
            this.skeleton.setSkin("default");
        }
        this.skeleton.setSlotsToSetupPose();

        this.stateData = new AnimationStateData(data);
        this.state = new AnimationState(this.stateData);

        this.state.setAnimation(0, "Idle", true);

        this.state.update(0);
        this.state.apply(this.skeleton);
        this.skeleton.updateWorldTransform();
    }


    // --- 渲染逻辑 (核心重写) ---

    @Override
    public void render(SpriteBatch sb) {
        this.stance.render(sb);

        // 战斗内绘制血条和球
        if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.MonsterRoom) && !this.isDead) {
            renderHealth(sb);
            if (!this.orbs.isEmpty()) {
                for (com.megacrit.cardcrawl.orbs.AbstractOrb o : this.orbs) {
                    o.render(sb);
                }
            }
        }

        // 判断是否是篝火 (Goldenglow 逻辑)
        if (!(AbstractDungeon.getCurrRoom() instanceof RestRoom)) {
            renderPlayerImage(sb); // 绘制 Spine
            this.hb.render(sb);
            this.healthHb.render(sb);
        } else {
            // 篝火休息时绘制静态肩膀图
            sb.setColor(Color.WHITE);
            renderShoulderImg(sb);
        }
    }

    @Override
    public void renderPlayerImage(SpriteBatch sb) {
        if (this.skeleton == null) return;

        // 更新时间
        this.state.update(Gdx.graphics.getDeltaTime());
        this.state.apply(this.skeleton);
        this.skeleton.updateWorldTransform();

        // 同步位置
        this.skeleton.setPosition(
                (this.drawX + this.animX) * Settings.scale,
                (this.drawY + this.animY) * Settings.scale
        );

        // 同步颜色
        this.skeleton.setColor(this.tint.color);

        // 处理翻转
        float absScaleX = Math.abs(this.skeleton.getScaleX());
        float absScaleY = Math.abs(this.skeleton.getScaleY());
        this.skeleton.setScaleX(this.flipHorizontal ? -absScaleX : absScaleX);
        this.skeleton.setScaleY(this.flipVertical ? -absScaleY : absScaleY);

        // 切换到 PolygonSpriteBatch 进行绘制
        sb.end();
        psb.begin();
        sr.draw(psb, this.skeleton); // 使用静态 renderer
        psb.end();
        sb.begin();
    }

    // --- 动画触发 ---

    @Override
    public void playDeathAnimation() {
        if (this.state != null) {
            this.state.setAnimation(0, "Die", false);
        }
    }

    @Override
    public void useFastAttackAnimation() {
        if (this.state != null) {
            this.state.setAnimation(0, "Attack", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    // 自定义辅助方法
    public void playAnimation(String name) {
        if (this.state != null) {
            this.state.setAnimation(0, name, false);
        }
    }

    // --- 其他配置 ---
    @Override public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(Strike.ID); retVal.add(Strike.ID); retVal.add(Strike.ID); retVal.add(Strike.ID); retVal.add(Strike.ID);
        retVal.add(Defend.ID); retVal.add(Defend.ID); retVal.add(Defend.ID);
        retVal.add(Resolute.ID);
        return retVal;
    }
    @Override public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(PenanceBasicRelic.ID);
        return retVal;
    }
    @Override public AbstractCard getStartCardForEvent() { return new Resolute(); }
    @Override public CharSelectInfo getLoadout() {
        return new CharSelectInfo(getNames()[0], getText()[0], MAX_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(), getStartingDeck(), false);
    }
    @Override public AbstractPlayer newInstance() { return new Penance(); }
    @Override public int getAscensionMaxHPLoss() { return 4; }
    @Override public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] { AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.SMASH, AbstractGameAction.AttackEffect.BLUNT_HEAVY };
    }
    private final Color penanceThemeColor = CardHelper.getColor(71, 63, 34);
    private final Color PenanceEffectColor = new Color(218f/255f, 165f/255f, 32f/255f, 1f);
    @Override public Color getCardRenderColor() { return penanceThemeColor; }
    @Override public Color getCardTrailColor() { return PenanceEffectColor; }
    @Override public Color getSlashAttackColor() { return PenanceEffectColor; }
    @Override public BitmapFont getEnergyNumFont() { return FontHelper.energyNumFontRed; }
    @Override public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA(Sounds.PENANCE_SELECT, 0.0F);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }
    @Override public String getCustomModeCharacterButtonSoundKey() { return Sounds.PENANCE_SELECT; }
    @Override public String getLocalizedCharacterName() { return getNames()[0]; }
    @Override public String getTitle(PlayerClass playerClass) { return getNames()[1]; }
    @Override public String getSpireHeartText() { return getText()[1]; }
    @Override public String getVampireText() { return getText()[2]; }
    @Override public AbstractCard.CardColor getCardColor() { return Meta.CARD_COLOR; }
}