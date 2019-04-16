package dev.marshall.visualsearch

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.marshall.visualsearch.Auth.SignInFragment
import dev.marshall.visualsearch.Database.DataSource.CartRepository
import dev.marshall.visualsearch.Database.DataSource.WishListRepository
import dev.marshall.visualsearch.Database.Local.CartDataSource
import dev.marshall.visualsearch.Database.Local.VSRoomDatabase
import dev.marshall.visualsearch.Database.Local.WishListDataSource
import dev.marshall.visualsearch.staggeredgridlayout.ProductGridItemDecoration
import kotlinx.android.synthetic.main.cart.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import dev.marshall.visualsearch.adapter.CartAdapter
import dev.marshall.visualsearch.Database.Model.Cart
import dev.marshall.visualsearch.Database.Model.WishList
import dev.marshall.visualsearch.adapter.CartSheetAdapter
import dev.marshall.visualsearch.model.Model_product
import dev.marshall.visualsearch.utils.Utils
import dev.marshall.visualsearch.viewholder.StaggeredProductCardViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.backdrop.view.*
import kotlinx.android.synthetic.main.cart.*
import java.text.DecimalFormat


class HomeFragment : Fragment() {

    var cartList: List<Cart> = ArrayList()
    var cartAdapter: CartAdapter? = null
    var cartSheetAdapter: CartSheetAdapter? = null
    lateinit var adapter:FirebaseRecyclerAdapter<Model_product, StaggeredProductCardViewHolder>
    lateinit var recyclercart: RecyclerView
    val auth=FirebaseAuth.getInstance()
    lateinit var recycler_cart_sheet:RecyclerView
    lateinit var compositeDisposable:CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Set up the tool bar
        setUpToolbar(view)

        compositeDisposable = CompositeDisposable()
        initDB()



        val wait =view.findViewById<ContentLoadingProgressBar>(R.id.wait)
        wait.visibility=View.VISIBLE
        val constraintLayout=view.findViewById<ConstraintLayout>(R.id.bottomsheet_cart)
        val layoutcollapsed=view.findViewById<ConstraintLayout>(R.id.layout_collapsed)
        val layoutexpanded=view.findViewById<ConstraintLayout>(R.id.layoutcart_expanded)
        val sheetBehavior=BottomSheetBehavior.from(constraintLayout)

        recyclercart=view.findViewById<RecyclerView>(R.id.recycler_cart)
        recyclercart.layoutManager=LinearLayoutManager(activity)
        recyclercart.hasFixedSize()
        recycler_cart_sheet=view.findViewById(R.id.recycler_cart_sheet)
        recycler_cart_sheet.hasFixedSize()



        //checkout
        view.checkout.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.checkoutFragment))

        view.collapse.setOnClickListener {
            sheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
        }

        view.wishlistFragment.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.wishlistFragment))

        view.myorderFragment.setOnClickListener {
            val user = auth.currentUser
            if (user!=null){
                Navigation.findNavController(it).navigate(R.id.myorderFragment)
            }
            else{
                val signInFragment=SignInFragment()
                signInFragment.show(childFragmentManager,signInFragment.tag)
            }
        }

        view.accountFragment.setOnClickListener{
            val user = auth.currentUser
            if (user!=null){
              Navigation.findNavController(it).navigate(R.id.accountFragment)
            }
            else{
                val signInFragment=SignInFragment()
                signInFragment.show(childFragmentManager,signInFragment.tag)
            }
        }



        sheetBehavior.setBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
               when(newState){
                   BottomSheetBehavior.STATE_HIDDEN ->{
                   }
                   BottomSheetBehavior.STATE_EXPANDED ->{
                       layoutcollapsed.visibility=View.INVISIBLE
                       layoutexpanded.visibility=View.VISIBLE
                   }
                   BottomSheetBehavior.STATE_COLLAPSED->{
                       layoutcollapsed.visibility=View.VISIBLE
                       layoutexpanded.visibility=View.INVISIBLE
                   }
                   BottomSheetBehavior.STATE_DRAGGING->{
                   }
                   BottomSheetBehavior.STATE_SETTLING ->{
                   }
                   BottomSheetBehavior.STATE_HALF_EXPANDED->{
                   }
               }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        //view
        view.image_visual.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.visualSearchFragment))

        // Set up the RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 3 == 2) 2 else 1
            }
        }


        val query = FirebaseDatabase.getInstance()
                .reference
                .child("Products")
                .limitToLast(100)

        val options=FirebaseRecyclerOptions.Builder<Model_product>()
                .setQuery(query,Model_product::class.java)
                .build()

        loadCartItems()

        adapter= object :FirebaseRecyclerAdapter<Model_product, StaggeredProductCardViewHolder>(options){
            override fun getItemViewType(position: Int): Int {
                return position % 3
            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredProductCardViewHolder {
                var layoutId = R.layout.staggered_product_card_first
                if (viewType == 1) {
                    layoutId = R.layout.staggered_product_card_second
                } else if (viewType == 2) {
                    layoutId = R.layout.staggered_product_card_third
                }

                val layoutView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
                return StaggeredProductCardViewHolder(layoutView)
            }

            override fun onBindViewHolder(holder: StaggeredProductCardViewHolder, position: Int, model: Model_product) {
                wait.visibility=View.INVISIBLE
                holder.productPrice.text="KES "+model.price.toString()
                holder.productTitle.text= model.title

                val key=adapter.getRef(position).key

                context?.let { Glide.with(it).load(model.imUrl).into(holder.productImage) }

                holder.itemView.setOnClickListener {

                }

                if (Utils.wishListRepository.isFavourite(key!!.toInt())==1){
                    holder.add_wishlist.setImageResource(R.drawable.ic_favorite)
                }
                else{
                    holder.add_wishlist.setImageResource(R.drawable.ic_favorite_border)
                }


                val wishList=WishList()
                wishList.id= key.toInt()
                wishList.image=model.imUrl
                wishList.name=model.title
                wishList.price= model.price.toString()



                holder.add_wishlist.setOnClickListener {
                    if (Utils.wishListRepository.isFavourite(key.toInt()) == 1){
                        Utils.wishListRepository.delete(wishList)
                        holder.add_wishlist.setImageResource(R.drawable.ic_favorite_border)

                    }
                    else{
                        holder.add_wishlist.setImageResource(R.drawable.ic_favorite)
                        Utils.wishListRepository.insertFav(wishList)
                    }

                }

                holder.add_cart.setOnClickListener {
                    val cart=Cart()
                    cart.image=model.imUrl
                    cart.name=model.title
                    cart.amount=1
                    cart.price=model.price

                    Utils.cartRepository.insertToCart(cart)
                }

            }

        }
        recyclerView.layoutManager = gridLayoutManager

        val largePadding = resources.getDimensionPixelSize(R.dimen.staggered_product_grid_spacing_large)
        val smallPadding = resources.getDimensionPixelSize(R.dimen.staggered_product_grid_spacing_small)
        recyclerView.addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))


        recyclerView.adapter=adapter
        adapter.notifyDataSetChanged()

        return view
    }



    private fun loadCartItems() {
        compositeDisposable.add(
                Utils.cartRepository.cartItems
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe { carts -> displayCartItem(carts) }
        )
    }

    private fun displayCartItem(carts: List<Cart>) {
        cartList=carts
        cartAdapter= CartAdapter(activity,carts)
        cartSheetAdapter= CartSheetAdapter(activity,carts)
        recycler_cart_sheet.adapter=cartSheetAdapter
        recyclercart.adapter = cartAdapter
        items.text= cartAdapter!!.itemCount.toString()+" ITEMS"

        view!!.subtotal.text= Utils.cartRepository.sumPrice().toString()
        val double=0.1* Utils.cartRepository.sumPrice()
        val shipping=0.1* Utils.cartRepository.sumPrice()
        val total=shipping+double+ Utils.cartRepository.sumPrice()

        view!!.tax.text= DecimalFormat("#.##").format(double)
        view!!.shipping.text=DecimalFormat("#.##").format(shipping)
        view!!.total_price.text="KES "+DecimalFormat("#.##").format(total)
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        compositeDisposable.clear()
    }

    override fun onResume() {
        adapter.startListening()
        super.onResume()
        loadCartItems()
    }
    private fun initDB() {
        Utils.vsRoomDatabase= VSRoomDatabase.getInstance(activity)
        Utils.cartRepository= CartRepository.getInstance(CartDataSource.getInstance(Utils.vsRoomDatabase.cartDAO()))
        Utils.wishListRepository= WishListRepository.getInstance(WishListDataSource.getInstance(Utils.vsRoomDatabase.favouriteDAO()))
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.app_bar)
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener(NavigationIconClickListener(
                context,
                view.findViewById(R.id.product_grid),
                AccelerateDecelerateInterpolator(),
                context!!.resources.getDrawable(R.drawable.branded_menu,null), // Menu open icon
                context!!.resources.getDrawable(R.drawable.close_menu,null))) // Menu close icon
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val user=auth.currentUser
        if (user!=null){
            menu.findItem(R.id.sign_in).title="Sign out"
        }
        else
            menu.findItem(R.id.sign_in).title="Sign In"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_in -> {
                val user=auth.currentUser
                if (user!=null){
                    FirebaseAuth.getInstance().signOut()
                    SweetAlertDialog(activity!!, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success!")
                            .setContentText("Sign out successful!")
                            .show()
                }
                else{
                    val bottomSheetSignIn=SignInFragment()
                    bottomSheetSignIn.show(childFragmentManager,bottomSheetSignIn.tag)
                }
            }
            R.id.setting -> {

            }
        }
        return true

    }



}