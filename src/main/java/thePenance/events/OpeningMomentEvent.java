package thePenance.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.core.Settings;
import thePenance.PenanceMod;
import thePenance.relics.CarnivalMoment;

import java.util.ArrayList;

public class OpeningMomentEvent extends AbstractImageEvent {
    public static final String ID = PenanceMod.makeID("OpeningMomentEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int curScreen = 0;
    private static final int COST = 150;

    // 状态机，用于处理连续选牌
    private enum State {
        CHOOSING,
        UPGRADING,
        REMOVING,
        FINISHED
    }
    private State state = State.CHOOSING;

    public OpeningMomentEvent() {
        super(NAME, DESCRIPTIONS[0], "thePenance/images/events/opening_moment.jpg");

        // 选项 1: [加入狂欢] 消耗150金币，获得遗物 狂欢时刻。
        if (AbstractDungeon.player.gold >= COST) {
            this.imageEventText.setDialogOption(OPTIONS[0] + COST + OPTIONS[1], new CarnivalMoment());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[2] + COST + OPTIONS[3], true); // 钱不够，禁用
        }

        // 选项 2: [保持警惕] 升级 一张卡， 从牌组中移除 一张卡。
        // 检查是否有卡可升级且有卡可删（防止极端情况崩溃）
        boolean canUpgrade = !AbstractDungeon.player.masterDeck.getUpgradableCards().isEmpty();
        boolean canRemove = !AbstractDungeon.player.masterDeck.getPurgeableCards().isEmpty();

        if (canUpgrade && canRemove) {
            this.imageEventText.setDialogOption(OPTIONS[4]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5], true); // 无法满足条件
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (curScreen) {
            case 0:
                switch (buttonPressed) {
                    case 0: // 加入狂欢
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseGold(COST);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new CarnivalMoment());

                        finishEvent();
                        break;
                    case 1: // 保持警惕
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);

                        // 第一步：打开升级界面
                        this.state = State.UPGRADING;
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 1, OPTIONS[6], true, false, false, false);

                        finishEvent();
                        break;
                }
                break;
            case 1:
                openMap();
                break;
        }
    }

    private void finishEvent() {
        this.curScreen = 1;
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[7]); // [离开]
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {

            // 处理升级逻辑
            if (this.state == State.UPGRADING) {
                if (this.state == State.UPGRADING) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

                    // 1. 执行升级逻辑
                    c.upgrade();
                    // 检查是否影响瓶装卡（虽然是事件，但这是好习惯）
                    AbstractDungeon.player.bottledCardUpgradeCheck(c);

                    // 2. 播放特效
                    // 播放绿色升级闪光
                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                    // 展示卡牌（使用 makeStatEquivalentCopy 以防止直接操作原卡对象导致显示位置错乱）
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));

                    // 清空选择
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    // 3. 状态流转：升级完成后，立刻打开删除界面
                    this.state = State.REMOVING;
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[8], false, false, false, true);

                } else if (this.state == State.REMOVING) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    this.state = State.FINISHED;
                }
            }
        }
    }
}