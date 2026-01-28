package thePenance.character;

import basemod.BaseMod;
import basemod.abstracts.CustomEnergyOrb;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import thePenance.cards.Defend;
import thePenance.cards.Resolute;
import thePenance.cards.Strike;
import thePenance.relics.PenanceBasicRelic;
import thePenance.util.Sounds;
import java.util.ArrayList;
import java.util.List;

import static thePenance.PenanceMod.characterPath;
import static thePenance.PenanceMod.makeID;

public class Penance extends CustomPlayer {
    // 角色基础数值
    public static final int ENERGY_PER_TURN = 3;
    public static final int MAX_HP = 80;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;

    // 字符串
    private static final String ID = makeID("Penance");
    private static String[] getNames() { return CardCrawlGame.languagePack.getCharacterString(ID).NAMES; }
    private static String[] getText() { return CardCrawlGame.languagePack.getCharacterString(ID).TEXT; }

    // 图片资源
    private static final String SHOULDER_1 = characterPath("shoulder.png");
    private static final String SHOULDER_2 = characterPath("shoulder2.png");

    // 能量球纹理
    private static final String[] orbTextures = {
            characterPath("energyorb/layer1.png"), characterPath("energyorb/layer2.png"),
            characterPath("energyorb/layer3.png"), characterPath("energyorb/layer4.png"),
            characterPath("energyorb/layer5.png"), characterPath("energyorb/cover.png"),
            characterPath("energyorb/layer1d.png"), characterPath("energyorb/layer2d.png"),
            characterPath("energyorb/layer3d.png"), characterPath("energyorb/layer4d.png"),
            characterPath("energyorb/layer5d.png")
    };
    private static final float[] layerSpeeds = new float[] { -20.0F, 20.0F, -40.0F, 40.0F, 360.0F };

    // --- 静态 Meta 类 (保留用于 BaseMod 注册) ---
    public static class Meta {
        @SpireEnum public static PlayerClass PENANCE;
        @SpireEnum(name = "PENANCE_COLOR") public static AbstractCard.CardColor CARD_COLOR;
        @SpireEnum(name = "PENANCE_COLOR") @SuppressWarnings("unused") public static CardLibrary.LibraryType LIBRARY_COLOR;

        // 注册相关路径
        private static final String CHAR_SELECT_BUTTON = "thePenance/char/select.png";
        private static final String CHAR_SELECT_PORTRAIT = "thePenance/char/penance.png";

        // 卡牌背景
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
            BaseMod.addColor(CARD_COLOR, cardColor,
                    BG_ATTACK, BG_SKILL, BG_POWER, ENERGY_ORB,
                    BG_ATTACK_P, BG_SKILL_P, BG_POWER_P, ENERGY_ORB_P,
                    SMALL_ORB);
        }

        public static void registerCharacter() {
            BaseMod.addCharacter(new Penance(), CHAR_SELECT_BUTTON, CHAR_SELECT_PORTRAIT, Meta.PENANCE);
        }
    }

    // --- 构造方法 ---
    public Penance() {
        super(getNames()[0], Meta.PENANCE,
                new CustomEnergyOrb(orbTextures, characterPath("energyorb/vfx.png"), layerSpeeds),
                null, null);

        initializeClass(null, SHOULDER_2, SHOULDER_1, null, getLoadout(),
                20.0F, -30.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));

        // 核心修改：从 PenanceSkinHelper 获取当前皮肤配置
        PenanceSkinHelper.SkinInfo activeSkin = PenanceSkinHelper.getCurrentSkin();

        // 加载实际的游戏内动画
        loadAnimation(activeSkin.atlas, activeSkin.json, activeSkin.scale);

        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Idle", "Attack", 0.1f);

        dialogX = (drawX + 0.0F * Settings.scale);
        dialogY = (drawY + 220.0F * Settings.scale);
    }

    // --- 角色核心逻辑 (Override Methods) ---

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(Strike.ID); retVal.add(Strike.ID); retVal.add(Strike.ID); retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Defend.ID); retVal.add(Defend.ID); retVal.add(Defend.ID);
        retVal.add(Resolute.ID);
        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(PenanceBasicRelic.ID);
        return retVal;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Resolute();
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(getNames()[0], getText()[0],
                MAX_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this,
                getStartingRelics(), getStartingDeck(), false);
    }

    @Override
    public AbstractPlayer newInstance() {
        return new Penance();
    }

    @Override
    public void useFastAttackAnimation() {
        this.state.setAnimation(0, "Attack", false);
        this.state.setAnimation(0, "Idle", true);
    }

    @Override
    public void playDeathAnimation() {
        this.state.setAnimation(0, "Die", false);
    }

    // --- 视觉效果与文本配置 ---

    @Override
    public int getAscensionMaxHPLoss() { return 4; }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.BLUNT_HEAVY,
                AbstractGameAction.AttackEffect.SMASH,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        };
    }

    @Override
    public List<CutscenePanel> getCutscenePanels() {
        List<CutscenePanel> panels = new ArrayList<>();
        panels.add(new CutscenePanel("thePenance/images/scenes/penance.jpg", "ATTACK_HEAVY"));
        // 添加第一张图
        // 参数1：图片路径
        // 参数2：播放该图时的音效 ID (可选，比如 "ATTACK_HEAVY", "DOOR_OPEN" 等)
        panels.add(new CutscenePanel("thePenance/images/scenes/vic1.png", "ATTACK_HEAVY"));

        // 添加第二张图
        panels.add(new CutscenePanel("thePenance/images/scenes/vic2.png"));

        // 添加第三张图
        panels.add(new CutscenePanel("thePenance/images/scenes/vic3.png"));

        return panels;
    }

    private final Color penanceThemeColor = CardHelper.getColor(71, 63, 34);
    private final Color PenanceEffectColor = new Color(218f/255f, 165f/255f, 32f/255f, 1f);

    @Override public Color getCardRenderColor() { return penanceThemeColor; }
    @Override public Color getCardTrailColor() { return PenanceEffectColor; }
    @Override public Color getSlashAttackColor() { return PenanceEffectColor; }
    @Override public BitmapFont getEnergyNumFont() { return FontHelper.energyNumFontRed; }

    @Override
    public void doCharSelectScreenSelectEffect() {
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