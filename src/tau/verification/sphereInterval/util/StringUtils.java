package tau.verification.sphereInterval.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils {

    public static <T> String collectionWithSeparatorToString(Collection<T> collection, String separator) {
        StringBuilder result = new StringBuilder();

        for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
            T elem = iterator.next();
            result.append(elem.toString());
            if (iterator.hasNext()) {
                result.append(separator);
            }
        }

        return result.toString();
    }
}