var React = require("react");
var omniscient = require("omniscient");
var component = omniscient.withDefaults({ jsx: true });

// Omniscient components are primarily stateless and have just a render method.
// Create a mixin to implement with React methods.
var withInitialState = {
    getInitialState: function () {
        return {
            ts: new Date().getTime(),
            selectedVariationSku: this.props.accessory.get("variationSkuInCart") ||
                this.props.accessory.get("selectedProductSku")
        };
    }
};

// Component = (optional) name, [mixins], render method
var Accessory = component("Accessory", [withInitialState], function(props) {

    // Turn the Immutable Map into a JS object to have easier access to properties
    var accessory = props.accessory.toObject();
    var self = this;

    var selectVariationFn = (sku) => (
      () => this.setState({ selectedVariationSku: sku }));

    var toggleProductFn = () => {
        this.setState({ ts: new Date().getTime() });
        props.accessory.update("variationSkuInCart", (oldSku) => {
            var currentSku = this.state.selectedVariationSku;
            return (oldSku === currentSku) ? null : currentSku;  // remove : add
        });
    };

    var variations = props.accessory.get("variations");

    var variationDisplay = variations
            .map((v) => {
                var sku = v.get("sku");
                var ajaxUpdateUrl = "url:..?t=" + this.state.ts;
                var iframe = ajaxUpdateUrl;

                if (variations.count() === 1) {
                    return <div>Only iframe: {iframe}</div>;
                }

                return <li key={sku}>
                    {sku}:
                    &nbsp;
                    [<a
                        href="javascript:void(0)"
                        onClick={selectVariationFn(sku)}>
                        {(self.state.selectedVariationSku === sku)? "x" : "+"}
                    </a>]
                    &nbsp;
                    {iframe}
                </li>;
            });

    return <div style={{border: "1px solid black"}}>
            <div>
                <p style={{"color":"lightgrey"}}>
                   State: ts={this.state.ts}, sku={this.state.selectedVariationSku}
                </p>
                <p style={{ width:"16em", display: "inline-block" }}>
                   guid: {accessory.guid},
                   variationSkuInCart: {accessory.variationSkuInCart}
                </p>
                <a
                    href="javascript:void(0)"
                    onClick={toggleProductFn}>
                    [{(accessory.variationSkuInCart === this.state.selectedVariationSku) ?
                        "Remove" : "Add"}]
                </a>
            </div>
            <ul>
                {variationDisplay}
            </ul>
        </div>;
});

var AccessoryGroup = component("AccessoryGroup", function (props){
    var accessoriesDisplay = props.accessories.toArray().map(function(accessory, i){
        return <Accessory key={i} accessory={accessory} />;
    });
    return <section>
        <h4>{props.label}</h4>
        {accessoriesDisplay}
    </section>;
});

var Summary = component("Accessory", function(props){

    var accessoriesInCart = props.accessories
            .valueSeq()
            .flatten(1)
            .filter(function (accessory){ return accessory.get("variationSkuInCart"); })
    ;

    var accessoriesInCartDisplay = accessoriesInCart
            .map(function (a){
                return <li key={a.get("guid")}>guid#sku:
                    {a.get("guid")}#{a.get("variationSkuInCart")}
                  </li>;});

    return <section style={{border: "1px dashed red", width: "30%", float: "right"}}>
        <h4>Summary</h4>
        <ul>{accessoriesInCartDisplay}</ul>
    </section>;
});

var Page = component("Page", function (props){

    var groupsDisplay = props.data.entrySeq().map(function([label, accessories]){
        return <AccessoryGroup key={label} accessories={accessories} label={label}/>;
    });
    return <div>
            <div style={{width:"65%", float: "left"}}>{groupsDisplay}</div>
            <Summary accessories={props.data}/>
          </div>;
});

module.exports = Page;
