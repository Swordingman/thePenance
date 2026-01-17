package thePenance;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import thePenance.character.Penance;
import thePenance.events.*;
import thePenance.relics.*;
import thePenance.util.GeneralUtils;
import thePenance.util.KeywordInfo;
import thePenance.util.Sounds;
import thePenance.util.TextureLoader;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.megacrit.cardcrawl.core.Settings.language;

@SpireInitializer
public class PenanceMod implements
        EditCharactersSubscriber, // 订阅角色编辑事件
        EditStringsSubscriber,    // 订阅文本（本地化）编辑事件
        EditKeywordsSubscriber,   // 订阅关键字编辑事件
        AddAudioSubscriber,       // 订阅音频添加事件
        PostInitializeSubscriber, // 订阅初始化后处理事件（用于添加徽章等）
        EditCardsSubscriber,      // 订阅卡牌编辑事件
        EditRelicsSubscriber {    // 订阅遗物编辑事件

    public static ModInfo info;
    public static String modID; // 修改你的 pom.xml 文件来改变这个ID
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static final Logger logger = LogManager.getLogger(modID); // 用于向控制台输出日志

    // 这用于给卡牌、遗物等对象的ID添加前缀，
    // 以避免不同模组使用相同名称时发生冲突。
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    // 由于类顶部的 @SpireInitializer 注解，ModTheSpire 会自动调用此方法。
    public static void initialize() {
        new PenanceMod();

        // 注册角色的颜色（必须在早期完成）
        Penance.Meta.registerColor();
    }

    public PenanceMod() {
        BaseMod.subscribe(this); // 这会让 BaseMod 在适当的时候触发所有实现的接口方法。
        logger.info(modID + " subscribed to BaseMod.");
    }

    @Override
    public void receivePostInitialize() {
        // 加载游戏内模组菜单中使用的图标。
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        // 设置游戏内模组菜单中显示的模组信息。
        // 这些信息取自你的 pom.xml 文件。
        BaseMod.addEvent(RainyNightInspectionEvent.ID, RainyNightInspectionEvent.class, Exordium.ID);
        BaseMod.addEvent(OpeningMomentEvent.ID, OpeningMomentEvent.class, TheCity.ID);
        BaseMod.addEvent(GreyDealEvent.ID, GreyDealEvent.class, TheCity.ID);
        BaseMod.addEvent(VolsiniiCourtEvent.ID, VolsiniiCourtEvent.class, TheCity.ID);
        BaseMod.addEvent(CaseFile1184Event.ID, CaseFile1184Event.class, TheBeyond.ID);

        // 如果你想设置一个配置面板，就在这里进行。
        // 你可以在 BaseMod wiki 页面 "Mod Config and Panel" 找到相关信息。
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, null);
    }

    /*---------- 本地化 (Localization) ----------*/

    // 这用于根据语言加载适当的本地化文件。
    private static String getLangString()
    {
        return language.name().toLowerCase();
    }
    private static final String defaultLanguage = "eng"; // 默认语言为英语

    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    @Override
    public void receiveEditCards() {
        // AutoAdd 会自动扫描你的包，找到所有继承自 AbstractCard 的类并添加它们。
        // 这样你就不用手动一个个 new Card() 了。
        new AutoAdd(modID)
                .packageFilter(PenanceMod.class)
                .setDefaultSeen(true) // 默认在图鉴中可见
                .cards();
    }

    @Override
    public void receiveEditRelics() {
        // 注册到你的角色专属池子中
        // 这样只有你的角色能获得这个遗物（如果是初始遗物，这也确保了它归属于该角色）
        // 注意：这里需要确保 PenanceBasicRelic 这个类是存在的
        BaseMod.addRelicToCustomPool(new PenanceBasicRelic(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new Innocent(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new LetterOfGratitude(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new BloodstainedCloth(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new ThornboundCodex(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new ShopVoucher(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new PlasticMole(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new SiracusanWine(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelicToCustomPool(new CarnivalMoment(), Penance.Meta.CARD_COLOR);
        BaseMod.addRelic(new LittleGavel(), RelicType.SHARED);
        BaseMod.addRelic(new BlackUmbrella(), RelicType.SHARED);
        BaseMod.addRelic(new GoldenScales(), RelicType.SHARED);

        // 如果你想做一个所有角色都能用的遗物，使用下面这行：
        // BaseMod.addRelic(new PenanceStone(), RelicType.SHARED);
    }

    @Override
    public void receiveEditStrings() {
        /*
            首先，加载默认的本地化文件（通常是英语）。
            然后，如果当前语言不同，尝试加载该语言的本地化文件。
            这样做的结果是，如果特定语言缺失某些翻译，将使用默认语言（英语）作为替补。
            稍后加载关键字时也会使用相同的过程。
        */
        loadLocalization(defaultLanguage); // 对默认语言不进行异常捕获；你最好确保至少有一套能用的。
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            }
            catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocalization(String lang) {
        // 虽然这里加载了每种类型的本地化文件，但大多数文件只是框架，让你看看格式。
        // 如果你用不到某些文件，可以随意注释掉或删除。
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                localizationPath(lang, "MonstersStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                localizationPath(lang, "OrbStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                localizationPath(lang, "PotionStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            keyword.prep();
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    keyword.prep();
                    registerKeyword(keyword);
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
    }

    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(null, info.PROPER_NAME, info.NAMES, info.DESCRIPTION, info.COLOR);
        if (!info.ID.isEmpty())
        {
            keywords.put(info.ID, info);
        }
    }

    @Override
    public void receiveEditCharacters() {
        // 注册角色
        Penance.Meta.registerCharacter();
    }

    @Override
    public void receiveAddAudio() {
        // 自动加载音频
        loadAudio(Sounds.class);
    }

    private static final String[] AUDIO_EXTENSIONS = { ".ogg", ".wav", ".mp3" }; // 还有更多有效类型，但不值得在这里全部检查
    private void loadAudio(Class<?> cls) {
        try {
            Field[] fields = cls.getDeclaredFields();
            outer:
            for (Field f : fields) {
                int modifiers = f.getModifiers();
                // 检查字段是否为 public static String
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && f.getType().equals(String.class)) {
                    String s = (String) f.get(null);
                    if (s == null) { // 如果没有定义值（为null），则使用字段名确定路径
                        s = audioPath(f.getName());

                        for (String ext : AUDIO_EXTENSIONS) {
                            String testPath = s + ext;
                            if (Gdx.files.internal(testPath).exists()) {
                                s = testPath;
                                BaseMod.addAudio(s, s); // 注册音频
                                f.set(null, s); // 将生成的路径回写到字段中
                                continue outer;
                            }
                        }
                        throw new Exception("Failed to find an audio file \"" + f.getName() + "\" in " + resourcesFolder + "/audio; check to ensure the capitalization and filename are correct.");
                    }
                    else { // 否则，加载已定义的路径
                        if (Gdx.files.internal(s).exists()) {
                            BaseMod.addAudio(s, s);
                        }
                        else {
                            throw new Exception("Failed to find audio file \"" + s + "\"; check to ensure this is the correct filepath.");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("Exception occurred in loadAudio: ", e);
        }
    }

    // 这些方法用于生成资源文件夹中各个部分的正确文件路径。
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String audioPath(String file) {
        return resourcesFolder + "/audio/" + file;
    }
    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    /**
     * 根据包名检查预期的资源路径。
     */
    private static String checkResourcesPath() {
        String name = PenanceMod.class.getName(); // getPackage 在打补丁时可能不稳定，所以使用类名。
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);

        if (!resources.exists()) {
            throw new RuntimeException("\n\tFailed to find resources folder; expected it to be at  \"resources/" + name + "\"." +
                    " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                    "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                    "\tat the top of the " + PenanceMod.class.getSimpleName() + " java file.");
        }
        if (!resources.child("images").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'images' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "images folder is in the correct location.");
        }
        if (!resources.child("localization").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'localization' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "localization folder is in the correct location.");
        }

        return name;
    }

    /**
     * 这根据 ModTheSpire 存储的信息确定模组的 ID。
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(PenanceMod.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    // 1. 这是一个“中间人”，把旧的调用转发给模板自带的 imagePath
    public static String makePath(String resourcePath) {
        // resourcePath 比如是 "cards/Strike.png"
        // imagePath 会自动加上 "thePenance/images/"
        return imagePath(resourcePath);
    }

    // 2. 专门给卡牌用的简便方法 (可选)
    public static String makeCardPath(String cardName) {
        return imagePath("cards/" + cardName);
    }

    // 加入狼群诅咒标识
    @SpireEnum
    public static AbstractCard.CardTags CURSE_OF_WOLVES;
}