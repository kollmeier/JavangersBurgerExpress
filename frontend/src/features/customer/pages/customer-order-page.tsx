import {useOrders} from "@/hooks/use-orders.ts";
import {Flame, Utensils} from "lucide-react";

const CustomerPage: React.FC = () => {

    const {customerOrders: {data: orders}} = useOrders(10)

    return (
        <div className="grid grid-cols-2 h-full gap-4 text-black">
            <div className="h-full flex flex-wrap gap-4">
                <h2 className="text-lg flex-1 basis-full"><Flame /> Wird zubereitet</h2>
                {orders?.filter(o => o.status === "IN_PROGRESS").map(order =>
                    <div key={order.id} className="grid auto-cols-auto auto-rows-min gap-2">
                        <div className="text-lg font-heading">{order.orderNumber}</div>
                    </div>
                )}
            </div>
            <div className="border-l-1 pl-4 h-full flex flex-wrap gap-4">
                <h2 className="text-lg flex-1 basis-full"><Utensils /> Abholbereit</h2>
                {orders?.filter(o => o.status === "READY").map(order =>
                    <div key={order.id} className="grid auto-cols-auto auto-rows-min gap-2">
                        <div className="text-lg font-heading">{order.orderNumber}</div>
                    </div>
                )}
            </div>
        </div>
    )
}

export default CustomerPage;