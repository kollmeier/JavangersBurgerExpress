import {useOrders} from "@/hooks/use-orders.ts";
import BeButton from "@/components/ui/be-button.tsx";
import {ArrowRightFromLine} from "lucide-react";

const CashierPage: React.FC = () => {

    const {cashierOrders: {data: orders}, advanceCashierOrder} = useOrders(10)

    return (
        <div className="grid grid-cols-2 h-full gap-4 text-black">
            <div className="h-full">
                <h2 className="text-sm">Fertige Bestellungen</h2>
                {orders?.filter(o => o.status === "READY").map(order =>
                    <div key={order.id} className="grid auto-cols-auto auto-rows-min gap-2 border-t border-dotted pt-1 mt-1">
                        <div className="font-heading">{order.orderNumber}</div>
                        <BeButton variant="primary" className="animate-pulse" onClick={() => advanceCashierOrder.mutate(order.id ?? "")} icon={ArrowRightFromLine}>Abgeholt</BeButton>
                        {order.items?.map(item => <div key={item.id} className="col-span-2">{item.amount} x {item.item?.name}</div>)}
                    </div>
                )}
            </div>
            <div className="border-l-1 pl-4 h-full">
                <h2 className="text-sm">Abgeholte Bestellungen</h2>
                {orders?.filter(o => o.status === "DELIVERED").map(order =>
                    <div key={order.id} className="grid auto-cols-auto auto-rows-min gap-2 border-t border-dotted pt-1 mt-1">
                        <div className="font-heading">{order.orderNumber}</div>
                    </div>
                )}
            </div>
        </div>
    )
}

export default CashierPage;