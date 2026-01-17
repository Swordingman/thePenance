package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.WolfCurseHelper;
import thePenance.character.Penance;
import thePenance.util.CardStats;

import java.util.ArrayList;

public class SyracusanWolves extends BaseCard {

    public static final String ID = makeID("SyracusanWolves");

    private static final int COST = 1;

    public SyracusanWolves() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 定义要生成的数量
        int amount = 5;

        // 循环 5 次
        for (int i = 0; i < amount; i++) {
            // 1. 每次循环都获取一张新的随机狼之诅咒
            // (这样生成的5张牌可能是不同的，比如2张ContinuousRain，3张Dignity...)
            AbstractCard c = WolfCurseHelper.getRandomWolfCurse();

            // 2. 检查升级逻辑
            // 如果这张卡(this)升级了，生成出来的诅咒牌(c)也要升级
            if (this.upgraded) {
                c.upgrade();
            }

            // 3. 将这张牌洗入抽牌堆
            // 参数说明: new MakeTempCardInDrawPileAction(卡牌, 数量, 是否随机洗入, 是否播放动画)
            addToBot(new MakeTempCardInDrawPileAction(c, 1, true, true));
        }
    }
}