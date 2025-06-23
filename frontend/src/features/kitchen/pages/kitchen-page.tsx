import {useOrders} from "@/hooks/use-orders.ts";
import BeButton from "@/components/ui/be-button.tsx";
import {ArrowRightFromLine} from "lucide-react";

const KitchenPage: React.FC = () => {

    const {kitchenOrders: {data: orders}, advanceKitchenOrder} = useOrders(10)

    return (
        <div className="grid grid-cols-2 h-full gap-4 text-black">
            <div className="h-full">
                <h2 className="text-sm">Neue Bestellungen</h2>
                {orders?.filter(o => o.status === "PAID").map(order =>
                    <div key={order.id} className="grid auto-cols-auto auto-rows-min gap-2 border-t border-dotted pt-1 mt-1">
                        <div className="font-heading">{order.orderNumber}</div>
                        <BeButton variant="primary" className="animate-pulse" onClick={() => advanceKitchenOrder.mutate(order.id ?? "")} icon={ArrowRightFromLine}>Zubereiten</BeButton>
                        {order.items?.map(item => <div key={item.id} className="col-span-2">{item.amount} x {item.item?.name}</div>)}
                    </div>
                )}
            </div>
            <div className="border-l-1 pl-4 h-full">
                <h2 className="text-sm">Bestellungen in Bearbeitung</h2>
                {orders?.filter(o => o.status === "IN_PROGRESS").map(order =>
                    <div key={order.id} className="grid auto-cols-auto auto-rows-min gap-2 border-t border-dotted pt-1 mt-1">
                        <div className="font-heading">{order.orderNumber}</div>
                        <BeButton variant="secondary" onClick={() => advanceKitchenOrder.mutate(order.id ?? "")} icon={ArrowRightFromLine}>Fertig</BeButton>
                        {order.items?.map(item => <div key={item.id} className="col-span-2">{item.amount} x {item.item?.name}</div>)}
                    </div>
                )}
            </div>
        </div>
    )
}

export default KitchenPage;