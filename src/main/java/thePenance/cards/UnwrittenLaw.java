package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.UnwrittenLawPower;
import thePenance.util.CardStats;

import java.util.ArrayList;

public class UnwrittenLaw extends BaseCard {
    public static final String ID = makeID("UnwrittenLaw");
    private static final int COST = 1;
    private static final int UPG_COST = 0; // 1->0

    public UnwrittenLaw() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        setCostUpgrade(UPG_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 1. 寻找抽牌堆里的能力卡
                ArrayList<AbstractCard> powers = new ArrayList<>();
                for (AbstractCard c : p.drawPile.group) {
                    if (c.type == CardType.POWER) {
                        powers.add(c);
                    }
                }

                if (!powers.isEmpty()) {
                    // 2. 随机选一张
                    AbstractCard c = powers.get(AbstractDungeon.cardRandomRng.random(powers.size() - 1));

                    // 3. 抽到手牌 (如果手牌满了会进弃牌堆，逻辑上仍应生效)
                    if (p.hand.size() < 10) {
                        p.drawPile.moveToHand(c, p.drawPile);
                    } else {
                        p.drawPile.moveToDiscardPile(c);
                        p.createHandIsFullDialog();
                    }

                    // 4. 施加特殊 Buff 标记这张卡
                    addToTop(new ApplyPowerAction(p, p, new UnwrittenLawPower(p, c.uuid), 1));
                }

                this.isDone = true;
            }
        });
    }
}