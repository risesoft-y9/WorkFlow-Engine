

<script lang="ts">
	export default {
	    name: 'renderComponent',
	    functional: true,
	    props: {
	        render: Function,
			data: Object,
	    },
	    render(h, ctx){
			
	        return h.render(h.data.row,h.data);
	    }
	};
</script>