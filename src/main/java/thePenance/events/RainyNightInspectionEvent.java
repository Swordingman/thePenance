package thePenance.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import thePenance.PenanceMod;

public class RainyNightInspectionEvent extends AbstractImageEvent {
    public static final String ID = PenanceMod.makeID("RainyNightInspectionEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int curScreen = 0; // 0: 初始选择, 1: 结果展示

    public RainyNightInspectionEvent() {
        super(NAME, DESCRIPTIONS[0], "thePenance/images/events/rainy_night.jpg"); // 请替换对应的图片路径

        // 选项 1: [依法扣押] 获得 50 金币，获得 1瓶 随机药水。
        this.imageEventText.setDialogOption(OPTIONS[0] + 50 + OPTIONS[1]);

        // 选项 2: [视而不见] 移除 1张卡牌。
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (curScreen) {
            case 0:
                switch (buttonPressed) {
                    case 0: // 依法扣押
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.gainGold(50);
                        // 获得随机药水
                        if (AbstractDungeon.player.hasRelic("Sozu")) {
                            AbstractDungeon.player.getRelic("Sozu").flash();
                        } else {
                            AbstractDungeon.player.obtainPotion(PotionHelper.getRandomPotion());
                        }

                        this.curScreen = 1;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]); // [离开]
                        break;
                    case 1: // 视而不见
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        if (AbstractDungeon.isScreenUp) {
                            AbstractDungeon.dynamicBanner.hide();
                            AbstractDungeon.previousScreen = AbstractDungeon.screen;
                        }
                        // 打开删牌界面
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[4], false, false, false, true);

                        this.curScreen = 1;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]); // [离开]
                        break;
                }
                break;
            case 1:
                openMap();
                break;
        }
    }

    @Override
    public void update() {
        super.update();
        // 处理删牌逻辑
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}