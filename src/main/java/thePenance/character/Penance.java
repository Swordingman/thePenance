package thePenance.character;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomEnergyOrb;
import basemod.abstracts.CustomPlayer;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import thePenance.PenanceMod;
import thePenance.cards.Defend;
import thePenance.cards.Resolute;
import thePenance.cards.Strike;
import thePenance.relics.PenanceBasicRelic;
import thePenance.util.Sounds;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import java.util.ArrayList;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import static thePenance.PenanceMod.characterPath;
import static thePenance.PenanceMod.makeID;

public class Penance extends CustomPlayer {
    // 角色基础数值(stats)
    public static final int ENERGY_PER_TURN = 3; // 每回合能量
    public static final int MAX_HP = 80;         // 最大生命值
    public static final int STARTING_GOLD = 99;  // 初始金币
    public static final int CARD_DRAW = 5;       // 每回合抽牌数
    public static final int ORB_SLOTS = 0;       // 充能球栏位（比如机器人的球，斥罪通常不需要，设为0）

    // 字符串 (Strings)
    private static final String ID = makeID("Penance"); // 这个ID必须与CharacterStrings.json文件中的ID一致
    private static String[] getNames() { return CardCrawlGame.languagePack.getCharacterString(ID).NAMES; }
    private static String[] getText() { return CardCrawlGame.languagePack.getCharacterString(ID).TEXT; }
    private static final String CHAR_IMAGE = characterPath("penance.png");

    // 这个静态内部类是必须的，用于避免注册角色时Java类加载机制产生的某些怪异问题。
    public static class Meta {
        // 这些用于识别你的角色，以及你角色的卡牌颜色。
        // 图鉴颜色（Library color）基本上与卡牌颜色相同，但由于游戏机制的原因，两者都需要。
        @SpireEnum
        public static PlayerClass PENANCE;
        @SpireEnum(name = "PENANCE_COLOR") // 这两个名称必须匹配。请将其更改为适合你角色的唯一名称。
        public static AbstractCard.CardColor CARD_COLOR;
        @SpireEnum(name = "PENANCE_COLOR") @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;

        // 1. 定义一个简单的类来保存皮肤信息
        public static class SkinInfo {
            public String name;     // 皮肤名称（显示在按钮上）
            public String atlas;    // atlas 文件路径
            public String json;     // json 文件路径
            public float scale;     // 缩放比例

            public SkinInfo(String name, String atlas, String json, float scale) {
                this.name = name;
                this.atlas = atlas;
                this.json = json;
                this.scale = scale;
            }
        }

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

        // 3. 当前选中的皮肤索引 (默认为0)
        public static int currentSkinIndex = 0;

        // 角色选择界面图片
        private static final String CHAR_SELECT_BUTTON = "thePenance/char/select.png";
        private static final String CHAR_SELECT_PORTRAIT = "thePenance/char/penance.png";

        // 角色卡牌背景图片
        private static final String BG_ATTACK = characterPath("cardback/bg_attack.png");
        private static final String BG_ATTACK_P = characterPath("cardback/bg_attack_p.png");
        private static final String BG_SKILL = characterPath("cardback/bg_skill.png");
        private static final String BG_SKILL_P = characterPath("cardback/bg_skill_p.png");
        private static final String BG_POWER = characterPath("cardback/bg_power.png");
        private static final String BG_POWER_P = characterPath("cardback/bg_power_p.png");
        private static final String ENERGY_ORB = characterPath("cardback/energy_orb.png");
        private static final String ENERGY_ORB_P = characterPath("cardback/energy_orb_p.png");
        private static final String SMALL_ORB = characterPath("cardback/small_orb.png");

        // 这用于给*某些*图片着色，但**不是**实际的卡牌。要修改卡牌外观，请编辑cardback文件夹中的图片！
        private static final Color cardColor = new Color(71f/255f, 63f/255f, 34f/255f, 1f);

        // 将在主模组文件中使用的方法
        public static void registerColor() {
            BaseMod.addColor(CARD_COLOR, cardColor,
                    BG_ATTACK, BG_SKILL, BG_POWER, ENERGY_ORB,
                    BG_ATTACK_P, BG_SKILL_P, BG_POWER_P, ENERGY_ORB_P,
                    SMALL_ORB);
        }

        public static void registerCharacter() {
            BaseMod.addCharacter(new Penance(),
                    CHAR_SELECT_BUTTON,
                    CHAR_SELECT_PORTRAIT,
                    Meta.PENANCE
            );
        }
    }


    // 游戏内图片
    private static final String SHOULDER_1 = characterPath("shoulder.png"); // 肩部图1和2用于篝火休息处。
    private static final String SHOULDER_2 = characterPath("shoulder2.png");

    // 用于能量球的纹理
    private static final String[] orbTextures = {
            characterPath("energyorb/layer1.png"), // 当你有能量时
            characterPath("energyorb/layer2.png"),
            characterPath("energyorb/layer3.png"),
            characterPath("energyorb/layer4.png"),
            characterPath("energyorb/layer5.png"),
            characterPath("energyorb/cover.png"), // “容器”
            characterPath("energyorb/layer1d.png"), // 当你没有能量时
            characterPath("energyorb/layer2d.png"),
            characterPath("energyorb/layer3d.png"),
            characterPath("energyorb/layer4d.png"),
            characterPath("energyorb/layer5d.png")
    };

    // 能量球纹理每层旋转的速度。负数表示反向旋转。
    private static final float[] layerSpeeds = new float[] {
            -20.0F,
            20.0F,
            -40.0F,
            40.0F,
            360.0F
    };


    // 实际的角色类代码从这里开始

    public Penance() {
        super(getNames()[0], Meta.PENANCE,
                new CustomEnergyOrb(orbTextures, characterPath("energyorb/vfx.png"), layerSpeeds), // 能量球
                null, null); // 动画

        initializeClass(null,
                SHOULDER_2,
                SHOULDER_1,
                null,
                getLoadout(),
                20.0F, -30.0F, 220.0F, 290.0F, // 角色碰撞箱。x y 坐标，然后是宽度和高度。
                new EnergyManager(ENERGY_PER_TURN));

        Meta.SkinInfo activeSkin = Meta.SKINS[Meta.currentSkinIndex];

        loadAnimation(
                activeSkin.atlas,
                activeSkin.json,
                activeSkin.scale
        );
        // 设置初始动作 "Idle"
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Idle", "Attack", 0.1f);

        // 对话气泡的位置。你可以稍后根据需要进行调整。对于大多数角色来说，这些值就可以。
        dialogX = (drawX + 0.0F * Settings.scale);
        dialogY = (drawY + 220.0F * Settings.scale);
    }

    // 皮肤切换按钮的碰撞箱
    private static Hitbox skinHitbox = new Hitbox(200f * Settings.scale, 50f * Settings.scale);

    // 更新皮肤切换逻辑
    public static void updateSkin() {
        // 1. 安全检查：如果不在主菜单，或者没选中我们的角色，就不执行逻辑
        // 注意：CardCrawlGame.chosenCharacter 存储的是当前选中的角色枚举(Enum)
        if (CardCrawlGame.mainMenuScreen == null ||
                CardCrawlGame.mainMenuScreen.charSelectScreen == null ||
                CardCrawlGame.chosenCharacter != Meta.PENANCE) {
            return;
        }

        // 设置按钮位置
        float btnX = Settings.WIDTH / 2.0f + 200f * Settings.scale;
        float btnY = Settings.HEIGHT / 2.0f - 100f * Settings.scale;
        skinHitbox.move(btnX, btnY);
        skinHitbox.update();

        // 点击检测
        if (skinHitbox.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            skinHitbox.clickStarted = true;
        }

        if (skinHitbox.clicked) {
            skinHitbox.clicked = false;

            // --- 切换皮肤索引 ---
            Meta.currentSkinIndex++;
            if (Meta.currentSkinIndex >= Meta.SKINS.length) {
                Meta.currentSkinIndex = 0;
            }

            // --- 关键修改：使用反射获取当前的角色实例 ---

            // 1. 获取 CharacterSelectScreen 中的 options 列表 (这是一个私有变量)
            ArrayList<CharacterOption> options = ReflectionHacks.getPrivate(
                    CardCrawlGame.mainMenuScreen.charSelectScreen,
                    CharacterSelectScreen.class,
                    "options"
            );

            // 2. 遍历列表，找到属于“斥罪”的那个选项
            for (CharacterOption o : options) {
                // 3. 获取选项中的 'c' (Character) 变量 (这也是一个私有变量，代表角色实例)
                AbstractPlayer p = ReflectionHacks.getPrivate(o, CharacterOption.class, "c");

                // 4. 如果这个实例是 Penance，我们就修改它的动画
                if (p instanceof Penance) {
                    Penance penanceChar = (Penance) p;
                    Meta.SkinInfo nextSkin = Meta.SKINS[Meta.currentSkinIndex];

                    // 重新加载动画
                    penanceChar.loadAnimation(nextSkin.atlas, nextSkin.json, nextSkin.scale);
                    penanceChar.state.setAnimation(0, "Idle", true);
                    break; // 找到后就退出循环
                }
            }
        }
    }

    public static void renderSkin(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
        // 1. 安全检查 + 判定是否选中了斥罪
        if (CardCrawlGame.mainMenuScreen == null ||
                CardCrawlGame.mainMenuScreen.charSelectScreen == null ||
                CardCrawlGame.chosenCharacter != Meta.PENANCE) {
            return;
        }

        float btnX = skinHitbox.cX - skinHitbox.width / 2.0f;
        float btnY = skinHitbox.cY - skinHitbox.height / 2.0f;

        // 绘制按钮背景
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.REWARD_SCREEN_ITEM, btnX, btnY, skinHitbox.width, skinHitbox.height);

        // 绘制文字
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont,
                Meta.SKINS[Meta.currentSkinIndex].name, skinHitbox.cX, skinHitbox.cY, Color.WHITE);
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

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        // 初始牌组的卡牌ID列表。
        // 如果你想要多张相同的卡牌，必须多次添加它。
        retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Strike.ID);
        retVal.add(Defend.ID);
        retVal.add(Defend.ID);
        retVal.add(Defend.ID);
        retVal.add(Resolute.ID);

        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        // 初始遗物的ID。你可以有多个，但推荐只放一个。
        retVal.add(PenanceBasicRelic.ID);

        return retVal;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        // 这张卡用于地精翻牌配对游戏。
        // 它应该是一张非打击、非防御的基础卡，但并非强制要求。
        return new Resolute();
    }

    /*- 以下是你*可能*应该调整的方法，但不调整也可以。 -*/

    @Override
    public int getAscensionMaxHPLoss() {
        return 4; // 进阶14+时的最大生命值减少量
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        // 当你攻击心脏时，将使用这些攻击特效。
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.BLUNT_HEAVY,
                AbstractGameAction.AttackEffect.SMASH,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        };
    }

    private final Color penanceThemeColor = CardHelper.getColor(71, 63, 34);
    private final Color PenanceEffectColor = new Color(218f/255f, 165f/255f, 32f/255f, 1f);
    @Override
    public Color getCardRenderColor() {
        return penanceThemeColor;
    }

    @Override
    public Color getCardTrailColor() {
        return PenanceEffectColor;
    }

    @Override
    public Color getSlashAttackColor() {
        return PenanceEffectColor;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        // 用于显示当前能量的字体。
        // 原版角色使用 energyNumFontRed, Blue, Green, 和 Purple。
        // 可以制作自己的字体，但不方便。
        return FontHelper.energyNumFontRed;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        // 当你在角色选择屏幕点击该角色按钮时触发。
        // 查看 SoundMaster 获取现有音效的完整列表，或查看 BaseMod wiki 了解如何添加自定义音频。
        CardCrawlGame.sound.playA(Sounds.PENANCE_SELECT, 0.0F);
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        // 与 doCharSelectScreenSelectEffect 类似，但用于自定义模式屏幕。无震动。
        return Sounds.PENANCE_SELECT;
    }

    // 不要直接调整这四个方法，请调整 CharacterStrings.json 文件的内容。
    @Override
    public String getLocalizedCharacterName() {
        return getNames()[0];
    }
    @Override
    public String getTitle(PlayerClass playerClass) {
        return getNames()[1];
    }
    @Override
    public String getSpireHeartText() {
        return getText()[1];
    }
    @Override
    public String getVampireText() {
        return getText()[2]; // 通常，这段文本唯一的区别在于吸血鬼如何称呼玩家。
    }

    /*- 你应该不需要编辑以下的任何方法。 -*/

    // 这用于在角色选择屏幕上显示角色的信息。
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(getNames()[0], getText()[0],
                MAX_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this,
                getStartingRelics(), getStartingDeck(), false);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return Meta.CARD_COLOR;
    }

    @Override
    public AbstractPlayer newInstance() {
        // 创建你的角色类的一个新实例。
        return new Penance();
    }
}