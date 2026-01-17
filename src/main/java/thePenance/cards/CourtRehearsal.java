package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.util.CardStats;

import java.util.ArrayList;
import java.util.Map;

public class CourtRehearsal extends BaseCard {

    public static final String ID = makeID("CourtRehearsal");

    private static final int COST = 0;

    // 用来记录当前模仿的卡
    private AbstractCard mimicCard = null;

    public CourtRehearsal() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setSelfRetain(true);
        setExhaust(true);
    }

    // --- 核心机制：每回合变身 ---
    @Override
    public void atTurnStart() {
        super.atTurnStart();
        transform();
    }

    private void transform() {
        // 1. 获取所有斥罪的卡牌
        ArrayList<AbstractCard> pool = new ArrayList<>();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.color == Penance.Meta.CARD_COLOR
                    && !c.cardID.equals(ID) // 排除自己
                    && !c.tags.contains(CardTags.HEALING)
                    && c.type != CardType.STATUS
                    && c.type != CardType.CURSE) {
                pool.add(c);
            }
        }

        if (!pool.isEmpty()) {
            // 2. 随机选一张并升级
            AbstractCard target = pool.get(AbstractDungeon.cardRandomRng.random(pool.size() - 1)).makeCopy();
            target.upgrade();

            // 3. 记录为模仿对象
            this.mimicCard = target;

            // 4. “易容”：复制标准属性
            this.name = target.name;
            this.rawDescription = target.rawDescription;
            this.cost = target.cost;
            this.costForTurn = target.costForTurn;
            this.type = target.type;
            this.target = target.target;
            this.baseDamage = target.baseDamage;
            this.baseBlock = target.baseBlock;
            this.baseMagicNumber = target.baseMagicNumber;
            this.magicNumber = target.magicNumber;

            // 【已移除报错的 defaultSecondMagicNumber 相关代码】

            // 5. 复制 BaseCard 的所有自定义变量
            this.cardVariables.clear(); // 清空旧变量
            if (target instanceof BaseCard) {
                BaseCard targetBase = (BaseCard) target;
                // 遍历目标的变量列表，将其注册到这张卡上
                for (Map.Entry<String, LocalVarInfo> entry : targetBase.cardVariables.entrySet()) {
                    String key = entry.getKey();
                    LocalVarInfo info = entry.getValue();
                    // 将变量注册到当前卡牌，数值设为目标的当前基础值
                    this.setCustomVar(key, info.base, 0);
                }
            }

            // 6. 闪烁提醒
            this.flash();
            this.initializeDescription();

            // 7. 设置悬浮预览
            this.cardsToPreview = new CourtRehearsal();
            if (this.cardsToPreview instanceof CourtRehearsal) {
                ((CourtRehearsal)this.cardsToPreview).mimicCard = null;
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (mimicCard != null) {
            mimicCard.calculateCardDamage(m);
            mimicCard.use(p, m);
        }

        if (this.upgraded) {
            addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        if (mimicCard != null) {
            mimicCard.applyPowers();
            mimicCard.calculateCardDamage(mo);

            // 同步标准数值
            this.damage = mimicCard.damage;
            this.isDamageModified = mimicCard.isDamageModified;

            // 同步 BaseCard 自定义变量
            syncCustomVarsFromMimic();
        } else {
            super.calculateCardDamage(mo);
        }
    }

    @Override
    public void applyPowers() {
        if (mimicCard != null) {
            mimicCard.applyPowers();

            // 同步标准数值
            this.damage = mimicCard.damage;
            this.block = mimicCard.block;
            this.magicNumber = mimicCard.magicNumber;
            this.isDamageModified = mimicCard.isDamageModified;
            this.isBlockModified = mimicCard.isBlockModified;
            this.isMagicNumberModified = mimicCard.isMagicNumberModified;

            // 【已移除报错的 defaultSecondMagicNumber 相关代码】

            // 同步 BaseCard 自定义变量
            syncCustomVarsFromMimic();
        } else {
            super.applyPowers();
        }
    }

    // 辅助方法：将模仿卡计算好的自定义变量值同步过来
    private void syncCustomVarsFromMimic() {
        if (mimicCard instanceof BaseCard) {
            BaseCard targetBase = (BaseCard) mimicCard;
            // 遍历我自己目前拥有的变量（在 transform 时已经从目标那里复制了 key）
            for (String key : this.cardVariables.keySet()) {
                // 现在 getCustomVar 应该是 public 或 protected 的了
                LocalVarInfo myVar = this.getCustomVar(key);
                LocalVarInfo targetVar = targetBase.getCustomVar(key);

                if (myVar != null && targetVar != null) {
                    // 直接把模仿对象计算好的 value 赋给这张卡
                    myVar.value = targetVar.value;
                    myVar.forceModified = targetVar.isModified();
                }
            }
        }
    }
}