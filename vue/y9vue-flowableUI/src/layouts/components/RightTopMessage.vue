<template>
    <router-link class="indexlayout-top-message" to="/">
        <icon-svg :style="{ fontSize: '16px' }" type="message"></icon-svg>
        <el-badge :value="message" class="indexlayout-top-message-badge" type="danger" />
    </router-link>
</template>
<script lang="ts">
    import { computed, ComputedRef, defineComponent, onMounted } from 'vue';
    import IconSvg from './IconSvg';

    interface RightTopMessageSetupData {
        message: ComputedRef<number>;
    }

    export default defineComponent({
        name: 'RightTopMessage',
        components: {
            IconSvg
        },
        setup(): RightTopMessageSetupData {
            // const store = useStore<{user: UserStateType}>();

            const message = computed<number>(() => 2);

            onMounted(() => {
                // store.dispatch("user/fetchMessage");
            });

            return {
                message
            };
        }
    });
</script>
<style lang="scss" scoped>
    // @import '../../assets/css/global.scss';
    .indexlayout-top-message {
        height: $headerHeight;
        line-height: $headerHeight;
        /* display: inline-block; */
        display: inline;
        color: #c0c4cc;

        .indexlayout-top-message-badge {
            margin-left: -5px;
            margin-top: -20px;

            :deep(.el-badge__content) {
                border: 0;
            }
        }
    }
</style>
