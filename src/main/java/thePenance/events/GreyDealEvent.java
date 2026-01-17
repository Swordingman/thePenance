package thePenance.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import thePenance.PenanceMod;
import thePenance.cards.DeadlyEnemy;
import thePenance.relics.Innocent;

public class GreyDealEvent extends AbstractImageEvent {
    public static final String ID = PenanceMod.makeID("GreyDealEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0;

    public GreyDealEvent() {
        super(NAME, DESCRIPTIONS[0], "thePenance/images/events/court.jpg");

        // 选项1: 受禄 (200金, 凡庸)
        this.imageEventText.setDialogOption(OPTIONS[0] + "200" + OPTIONS[1], new com.megacrit.cardcrawl.cards.curses.Normality());
        // 选项2: 秉公执法 (无罪, 死敌)
        this.imageEventText.setDialogOption(OPTIONS[2], new DeadlyEnemy(), new Innocent());
        // 选项3: 无视
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // 受禄
                        AbstractDungeon.player.gainGold(200);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                                new com.megacrit.cardcrawl.cards.curses.Normality(),
                                Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        break;
                    case 1: // 秉公执法
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                                Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new Innocent());
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                                new DeadlyEnemy(),
                                Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        break;
                    case 2: // 离开
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        break;
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]); // "离开"
                screenNum = 1;
                break;
            case 1:
                openMap();
                break;
        }
    }
}