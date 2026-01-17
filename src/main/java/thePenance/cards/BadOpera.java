package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class BadOpera extends BaseCard {

    public static final String ID = makeID("BadOpera");

    private static final int COST = 1;

    public BadOpera() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < 2; i++) {
            // 随机生成一张攻击牌
            AbstractCard c = AbstractDungeon.returnTrulyRandomCardInCombat(CardType.ATTACK).makeCopy();

            // 如果本卡升级了，生成的卡也升级
            if (this.upgraded) {
                c.upgrade();
            }

            addToBot(new MakeTempCardInHandAction(c, 1));
        }
    }
}