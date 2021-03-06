package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;

import java.util.ArrayDeque;

//TODO: Average/Current!
public class TPSManager extends SubscriberImpl
{
    private final ArrayDeque<Float> queue = new ArrayDeque<>(20);
    private long time;
    private float tps;

    public TPSManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<SPacketTimeUpdate>>
                    (PacketEvent.Receive.class, SPacketTimeUpdate.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<SPacketTimeUpdate> event)
            {
                if (time != 0)
                {
                    if (queue.size() > 20)
                    {
                        queue.poll();
                    }

                    queue.add(20.0f
                            * (1000.0f / (System.currentTimeMillis() - time)));

                    float factor = 0.0f;
                    for (Float qTime : queue)
                    {
                        factor += Math.max(0.0f, Math.min(20.0f, qTime));
                    }

                    factor /= queue.size();
                    tps = factor;
                }

                time = System.currentTimeMillis();
            }
        });
    }

    public float getTps()
    {
        return tps;
    }

    public float getFactor()
    {
        return tps / 20.0f;
    }

}
