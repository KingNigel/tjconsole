package org.jsoftware.tjconsole.command.definition;

import jline.console.completer.Completer;
import org.jsoftware.tjconsole.DataOutputService;
import org.jsoftware.tjconsole.TJContext;
import org.jsoftware.tjconsole.command.CommandAction;
import org.jsoftware.tjconsole.console.Output;

import javax.management.MBeanAttributeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Command to get attribute value of mxBean
 *
 * @author szalik
 */
public class GetAttributeCommandDefinition extends AbstractCommandDefinition {
    public GetAttributeCommandDefinition() {
        super("Get attribute value", "get <attributeName>", "get", true);
    }


    @Override
    public CommandAction action(String input) throws Exception {
        final List<String> attributes = new ArrayList<String>(Arrays.asList(input.substring(prefix.length()).trim().split("[ ,]")));
        for (Iterator<String> it = attributes.iterator(); it.hasNext(); ) {
            if (it.next().trim().length() == 0) {
                it.remove();
            }
        }
        return new CommandAction() {
            @Override
            public void doAction(TJContext ctx, Output output) throws Exception {
                for (MBeanAttributeInfo ai : ctx.getAttributes()) {
                    if (skip(ai.getName())) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("@|cyan ").append(ai.getName()).append("|@ = @|yellow ");
                    Object value = ctx.getServer().getAttribute(ctx.getObjectName(), ai.getName());
                    DataOutputService.get(ai.getType()).output(value, ctx, sb);
                    sb.append(" |@");
                    output.println(sb.toString());
                    attributes.remove(ai.getName());
                }
                if (!attributes.isEmpty()) {
                    output.outError("Cannot find attributes: " + attributes);
                    ctx.fail(this, 20);
                }
            }

            private boolean skip(String aiName) {
                if (attributes.size() == 0) {
                    return false;
                }
                for (String a : attributes) {
                    if (a.equalsIgnoreCase(aiName)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    public Completer getCompleter(final TJContext ctx) {
        return new AbstractAttributeCompleter(ctx, prefix, "") {
            @Override
            protected boolean condition(MBeanAttributeInfo ai) {
                return ai.isReadable();
            }
        };
    }
}
