package tau.verification.sphereInterval.chaoticIteration;

import tau.verification.sphereInterval.transformer.BaseTransformer;

import javax.xml.transform.Transformer;
import java.util.ArrayList;
import java.util.List;

public class Equation {
    private final WorkListItem lhsWorkListItem;
    private final ArrayList<WorkListItem> rhsWorkListItems;
    private BaseTransformer transformer;

    private final String unitDescription;

    public Equation(WorkListItem lhsWorkListItem, BaseTransformer transformer, String unitDescription) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.transformer = transformer;
        this.rhsWorkListItems = new ArrayList<>();
        this.unitDescription = unitDescription;
    }

    public Equation(WorkListItem lhsWorkListItem, BaseTransformer transformer, WorkListItem argument, String unitDescription) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.transformer = transformer;

        this.rhsWorkListItems = new ArrayList<>();
        this.rhsWorkListItems.add(argument);

        this.unitDescription = unitDescription;
    }

    public Equation(WorkListItem lhsWorkListItem, BaseTransformer transformer, WorkListItem arg1, WorkListItem arg2, String unitDescription) {
        this.lhsWorkListItem = lhsWorkListItem;
        this.transformer = transformer;

        this.rhsWorkListItems = new ArrayList<>();
        this.rhsWorkListItems.add(arg1);
        this.rhsWorkListItems.add(arg2);

        this.unitDescription = unitDescription;
    }

    public WorkListItem getLhsWorkListItem() {
        return lhsWorkListItem;
    }

    public void updateTransformer(BaseTransformer transformer)
    {
        this.transformer = transformer;
    }

    public List<WorkListItem> getRhsWorkListItems() {
        return rhsWorkListItems;
    }

    public void evaluate() {
        this.lhsWorkListItem.value = this.transformer.invoke(rhsWorkListItems);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(lhsWorkListItem);
        stringBuilder.append(" = ");
        stringBuilder.append(transformer.invocationToString(rhsWorkListItems));

        int magicNumber = 50 - stringBuilder.toString().length();
        magicNumber = (magicNumber % 4) == 0 ? magicNumber : magicNumber + 1;

        for(int i = magicNumber/4; i >=0 ; i--) {
            stringBuilder.append("\t");
        }

        stringBuilder.append("// ");
        stringBuilder.append(unitDescription);

        return stringBuilder.toString();
    }
}