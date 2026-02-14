package thePenance.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import thePenance.PenanceMod;
import thePenance.cards.DifficultyOptionCard; // 引入刚才写的卡

import java.util.HashMap;
import java.util.Map;

public class PenanceDifficultyHelper {
    public enum DifficultyLevel {
        NORMAL(0), HARD(1), HELL(2);
        int index;
        DifficultyLevel(int i) { this.index = i; }
    }
    public static DifficultyLevel currentDifficulty = DifficultyLevel.NORMAL;

    // --- 核心变化：存储3张卡牌实例 ---
    private static Map<DifficultyLevel, AbstractCard> difficultyCards = new HashMap<>();

    // UI 控件
    private static Hitbox leftHb = new Hitbox(70.0f * Settings.scale, 70.0f * Settings.scale);
    private static Hitbox rightHb = new Hitbox(70.0f * Settings.scale, 70.0f * Settings.scale);

    public static void saveDifficulty() {
        try {
            PenanceMod.penanceConfig.setInt("difficulty", currentDifficulty.index);
            PenanceMod.penanceConfig.save();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void loadDifficulty() {
        try {
            if (PenanceMod.penanceConfig != null) {
                int index = PenanceMod.penanceConfig.getInt("difficulty");
                if (index < 0 || index >= DifficultyLevel.values().length) index = 0;
                currentDifficulty = DifficultyLevel.values()[index];
            }
        } catch (Exception e) { currentDifficulty = DifficultyLevel.NORMAL; }

        // 加载时初始化卡牌 (如果还没初始化)
        initializeCards();
    }

    // 初始化三张展示用的卡牌
    private static void initializeCards() {
        if (difficultyCards.isEmpty()) {
            difficultyCards.put(DifficultyLevel.NORMAL, new DifficultyOptionCard(DifficultyLevel.NORMAL));
            difficultyCards.put(DifficultyLevel.HARD, new DifficultyOptionCard(DifficultyLevel.HARD));
            difficultyCards.put(DifficultyLevel.HELL, new DifficultyOptionCard(DifficultyLevel.HELL));
        }
    }

    private static boolean isPenanceSelected() {
        if (CardCrawlGame.mainMenuScreen == null || CardCrawlGame.mainMenuScreen.charSelectScreen == null) return false;
        for (CharacterOption o : CardCrawlGame.mainMenuScreen.charSelectScreen.options) {
            if (o.selected && o.c instanceof Penance) return true;
        }
        return false;
    }

    public static void update() {
        if (!isPenanceSelected()) return;
        initializeCards(); // 双重保险

        // --- 位置计算 ---
        float centerX = Settings.WIDTH * 0.85f;
        float centerY = (Settings.HEIGHT * 0.65f) - (250.0f * Settings.scale);

        // 箭头稍微离远一点，因为卡牌比较宽
        float arrowOffset = 200.0f * Settings.scale;

        leftHb.move(centerX - arrowOffset, centerY);
        rightHb.move(centerX + arrowOffset, centerY);
        leftHb.update();
        rightHb.update();

        // --- 更新卡牌逻辑 ---
        AbstractCard card = difficultyCards.get(currentDifficulty);
        if (card != null) {
            card.target_x = centerX;
            card.target_y = centerY;
            card.drawScale = 0.75f; // 稍微缩小一点，或者设为 1.0f 原大小
            card.update();
            card.updateHoverLogic(); // 让鼠标放上去时有放大效果

            // 修正：如果不手动设为 untargeted，某些逻辑可能会报错，虽然这里应该没事
            // 主要是为了让卡牌知道鼠标在哪里
        }

        // --- 点击切换逻辑 ---
        if (InputHelper.justClickedLeft) {
            boolean changed = false;
            if (leftHb.hovered) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                int newIndex = currentDifficulty.index - 1;
                if (newIndex < 0) newIndex = DifficultyLevel.values().length - 1;
                currentDifficulty = DifficultyLevel.values()[newIndex];
                changed = true;
            } else if (rightHb.hovered) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                int newIndex = currentDifficulty.index + 1;
                if (newIndex >= DifficultyLevel.values().length) newIndex = 0;
                currentDifficulty = DifficultyLevel.values()[newIndex];
                changed = true;
            }

            if (changed) {
                saveDifficulty();
                // 切换时，重置一下新卡牌的动画状态，防止视觉跳变
                AbstractCard newCard = difficultyCards.get(currentDifficulty);
                newCard.current_x = centerX;
                newCard.current_y = centerY;
                newCard.target_x = centerX;
                newCard.target_y = centerY;
            }
        }
    }

    public static void render(SpriteBatch sb) {
        if (!isPenanceSelected()) return;

        // 获取当前卡牌
        AbstractCard card = difficultyCards.get(currentDifficulty);
        if (card != null) {
            card.render(sb);
        }

        // --- 绘制箭头 ---
        // 箭头颜色逻辑
        Color cLeft = Settings.GOLD_COLOR.cpy();
        if (leftHb.hovered) cLeft = Color.WHITE.cpy();
        sb.setColor(cLeft);
        sb.draw(ImageMaster.CF_LEFT_ARROW, leftHb.cX - 24.0f, leftHb.cY - 24.0f, 24.0f, 24.0f, 48.0f, 48.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 48, 48, false, false);

        Color cRight = Settings.GOLD_COLOR.cpy();
        if (rightHb.hovered) cRight = Color.WHITE.cpy();
        sb.setColor(cRight);
        sb.draw(ImageMaster.CF_RIGHT_ARROW, rightHb.cX - 24.0f, rightHb.cY - 24.0f, 24.0f, 24.0f, 48.0f, 48.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 48, 48, false, false);

        leftHb.render(sb);
        rightHb.render(sb);
    }
}