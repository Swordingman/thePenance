package thePenance.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import thePenance.PenanceMod;
import thePenance.relics.CarnivalMoment;

import java.util.ArrayList;

public class WolfCurseHelper {

    /**
     * 获取所有带有 CURSE_OF_WOLVES 标签的卡牌列表
     */
    public static ArrayList<AbstractCard> getAllWolfCurses() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        // 遍历游戏内所有注册的卡牌
        for (AbstractCard c : CardLibrary.getAllCards()) {
            // 检查是否有该标签，且不是原始的基础卡（避免拿到未初始化的数据，虽然一般没事）
            if (c.hasTag(PenanceMod.CURSE_OF_WOLVES)) {
                list.add(c.makeCopy());
            }
        }
        return list;
    }

    // 随机狼群诅咒
    public static AbstractCard getRandomWolfCurse() {
        ArrayList<AbstractCard> list = getAllWolfCurses();
        if (list.isEmpty()) {
            return new com.megacrit.cardcrawl.cards.curses.Clumsy();
        }
        AbstractCard randomCurse = list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(CarnivalMoment.ID)) {
            if (!randomCurse.upgraded) {
                randomCurse.upgrade();
            }
            AbstractDungeon.player.getRelic(CarnivalMoment.ID).flash();
        }
        return randomCurse;
    }

    // 选择狼群诅咒
    public static class ChooseAction extends AbstractGameAction {
        private final String tipMsg;

        public ChooseAction(int amount, String tipMsg) {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
            this.amount = amount;
            this.tipMsg = tipMsg;
        }

        @Override
        public void update() {
            // 阶段一：打开选择界面
            if (this.duration == Settings.ACTION_DUR_FAST) {
                // 直接调用外面的静态方法获取列表
                ArrayList<AbstractCard> sourceCards = WolfCurseHelper.getAllWolfCurses();
                CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

                boolean hasCarnivalRelic = AbstractDungeon.player.hasRelic(CarnivalMoment.ID);

                for (AbstractCard c : sourceCards) {
                    if (hasCarnivalRelic && c.canUpgrade()) {
                        c.upgrade();
                    }
                    group.addToTop(c);
                }

                if (group.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.gridSelectScreen.open(group, this.amount, this.tipMsg, false, false, false, false);
                tickDuration();
                return;
            }

            // 阶段二：处理选择结果
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    AbstractCard copy = c.makeStatEquivalentCopy();

                    if (AbstractDungeon.player.hasRelic(CarnivalMoment.ID) && !copy.upgraded) {
                        copy.upgrade();
                        AbstractDungeon.player.getRelic(CarnivalMoment.ID).flash();
                    }

                    if (AbstractDungeon.player.hand.size() < 10) {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(copy, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    } else {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(copy, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
                this.isDone = true;
            }
            tickDuration();
        }
    }
}