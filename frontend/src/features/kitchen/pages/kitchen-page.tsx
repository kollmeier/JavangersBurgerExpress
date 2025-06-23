import {useOrders} from "@/hooks/use-orders.ts";

const KitchenPage: React.FC = () => {

    const {data: orders} = useOrders(10).kitchenOrders

    return (
        <div className="grid grid-cols-2 h-full gap-4 text-black">
            <div className="h-full">
                <h2 className="text-sm">Neue Bestellungen</h2>
                {orders?.filter(o => o.status === "PAID").map(order => <>
                    <div key={order.id} className="border-t border-dotted pt-1 mt-1 font-heading">{order.orderNumber}</div>
                    {order.items?.map(item => <div key={item.id}>{item.amount} x {item.item?.name}</div>)}
                </>)}
            </div>
            <div className="border-l-1 pl-4 h-full">
                <h2 className="text-sm">Bestellungen in Bearbeitung</h2>
                {orders?.filter(o => o.status === "IN_PROGRESS").map(order => <>
                    <div key={order.id} className="border-t border-dotted pt-1 mt-1 font-heading">{order.orderNumber}</div>
                    {order.items?.map(item => <div key={item.id}>{item.amount} x {item.item?.name}</div>)}
                </>)}
            </div>
        </div>
    )
}

export default KitchenPage;