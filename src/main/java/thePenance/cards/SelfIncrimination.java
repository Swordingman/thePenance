package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

import java.util.ArrayList;

public class SelfIncrimination extends BaseCard {
    public static final String ID = makeID("SelfIncrimination");
    private static final int COST = 1;
    private static final int UPGRADE_COST = 0;

    public SelfIncrimination() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setExhaust(true);
        setCostUpgrade(UPGRADE_COST);
        // 预览衍生卡
        this.cardsToPreview = new ExhibitA();
        this.cardsToPreview = new Perjury();
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) return false;

        boolean hasCurse = false;
        for (AbstractCard c : p.hand.group) {
            if (c.type == CardType.CURSE || c.color == CardColor.CURSE) {
                hasCurse = true;
                break;
            }
        }

        if (!hasCurse) {
            this.cantUseMessage = "我需要消耗一张诅咒牌。";
            return false;
        }
        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 添加一个自定义动作来处理筛选和选择
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPlayer p = AbstractDungeon.player;
                // 1. 创建一个列表来暂存非诅咒牌
                ArrayList<AbstractCard> nonCurses = new ArrayList<>();

                // 2. 遍历手牌，把非诅咒牌找出来
                for (AbstractCard c : p.hand.group) {
                    if (c.type != CardType.CURSE && c.color != CardColor.CURSE) {
                        nonCurses.add(c);
                    }
                }

                // 3. 暂时从手牌中移除这些非诅咒牌
                // 注意：这里只是从 group 列表中移除，不要直接调用 p.hand.removeCard，因为可能会触发不需要的钩子
                p.hand.group.removeAll(nonCurses);

                // 4. 打开选择界面 (此时手牌里只有诅咒牌)
                if (p.hand.group.isEmpty()) {
                    // 理论上进不去这里（因为canUse检查过了），但为了安全防止空指针
                    p.hand.group.addAll(nonCurses);
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.handCardSelectScreen.open("选择一张诅咒牌消耗", 1, false, false, false, false);

                // 5. 添加后续动作来处理结果并归还手牌
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        // 处理选中的牌
                        if (!AbstractDungeon.handCardSelectScreen.selectedCards.isEmpty()) {
                            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                                // 消耗选中的牌
                                p.hand.moveToExhaustPile(c);
                            }
                            AbstractDungeon.handCardSelectScreen.selectedCards.clear();

                            // 给予衍生卡
                            addToTop(new MakeTempCardInHandAction(new Perjury()));
                            addToTop(new MakeTempCardInHandAction(new ExhibitA()));
                        }

                        // 6. 核心步骤：把暂存的非诅咒牌放回手牌
                        returnCards(p, nonCurses);

                        // 刷新手牌位置布局
                        p.hand.refreshHandLayout();
                        this.isDone = true;
                    }
                });

                this.isDone = true;
            }
        });
    }

    // 辅助方法：归还卡牌
    private void returnCards(AbstractPlayer p, ArrayList<AbstractCard> cardsToReturn) {
        for (AbstractCard c : cardsToReturn) {
            p.hand.addToTop(c);
        }
    }
}