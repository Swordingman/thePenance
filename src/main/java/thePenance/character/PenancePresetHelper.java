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
import thePenance.cards.PresetOptionCard;

import java.util.HashMap;
import java.util.Map;

public class PenancePresetHelper {
    public enum PresetLevel {
        DEFAULT(0),
        REHEARSAL(1),
        WOLVES(2),
        CURSES(3),
        DRINK(4),
        HEALTH(5),
        DEBATE(6);
        public int index;
        PresetLevel(int i) { this.index = i; }
    }
    public static PresetLevel currentPreset = PresetLevel.DEFAULT;

    private static Map<PresetLevel, AbstractCard> presetCards = new HashMap<>();

    // UI 控件
    private static Hitbox leftHb = new Hitbox(70.0f * Settings.scale, 70.0f * Settings.scale);
    private static Hitbox rightHb = new Hitbox(70.0f * Settings.scale, 70.0f * Settings.scale);

    public static void savePreset() {
        try {
            PenanceMod.penanceConfig.setInt("preset", currentPreset.index);
            PenanceMod.penanceConfig.save();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void loadPreset() {
        try {
            if (PenanceMod.penanceConfig != null) {
                int index = PenanceMod.penanceConfig.getInt("preset");
                if (index < 0 || index >= PresetLevel.values().length) index = 0;
                currentPreset = PresetLevel.values()[index];
            }
        } catch (Exception e) { currentPreset = PresetLevel.DEFAULT; }

        initializeCards();
    }

    private static void initializeCards() {
        if (presetCards.isEmpty()) {
            presetCards.put(PresetLevel.DEFAULT, new PresetOptionCard(PresetLevel.DEFAULT));
            presetCards.put(PresetLevel.REHEARSAL, new PresetOptionCard(PresetLevel.REHEARSAL));
            presetCards.put(PresetLevel.WOLVES, new PresetOptionCard(PresetLevel.WOLVES));
            presetCards.put(PresetLevel.CURSES, new PresetOptionCard(PresetLevel.CURSES));
            presetCards.put(PresetLevel.DRINK, new PresetOptionCard(PresetLevel.DRINK));
            presetCards.put(PresetLevel.HEALTH, new PresetOptionCard(PresetLevel.HEALTH));
            presetCards.put(PresetLevel.DEBATE, new PresetOptionCard(PresetLevel.DEBATE));
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
        initializeCards();

        float centerX = (Settings.WIDTH * 0.85f) - (550.0f * Settings.scale);
        float centerY = Settings.HEIGHT * 0.65f;
        float arrowOffset = 200.0f * Settings.scale;

        leftHb.move(centerX - arrowOffset, centerY);
        rightHb.move(centerX + arrowOffset, centerY);
        leftHb.update();
        rightHb.update();

        AbstractCard card = presetCards.get(currentPreset);
        if (card != null) {
            card.target_x = centerX;
            card.target_y = centerY;
            card.drawScale = 0.75f;
            card.update();
            card.updateHoverLogic();
        }

        if (InputHelper.justClickedLeft) {
            boolean changed = false;
            if (leftHb.hovered) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                int newIndex = currentPreset.index - 1;
                if (newIndex < 0) newIndex = PresetLevel.values().length - 1;
                currentPreset = PresetLevel.values()[newIndex];
                changed = true;
            } else if (rightHb.hovered) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                int newIndex = currentPreset.index + 1;
                if (newIndex >= PresetLevel.values().length) newIndex = 0;
                currentPreset = PresetLevel.values()[newIndex];
                changed = true;
            }

            if (changed) {
                savePreset();
                AbstractCard newCard = presetCards.get(currentPreset);
                newCard.current_x = centerX;
                newCard.current_y = centerY;
                newCard.target_x = centerX;
                newCard.target_y = centerY;
            }
        }
    }

    public static void render(SpriteBatch sb) {
        if (!isPenanceSelected()) return;

        AbstractCard card = presetCards.get(currentPreset);
        if (card != null) {
            card.render(sb);
        }

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