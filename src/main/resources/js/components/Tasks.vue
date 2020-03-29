<template>
    <div>
        <ul class="task-list max-w-4xl">
            <li class="py-1 list-disc" v-for="task in tasks" :key="task.id">
                <div class="view flex items-center">
                    <label class="text-lg font-light mx-6 flex-1 py-2 text-gray-700">
                        {{ task.body }}
                    </label>

                </div>
            </li>
        </ul>

        <div class="mt-10 max-w-5xl">
            <form @submit.prevent="createTask" >
                <input type="text" placeholder="Add a new task" v-model="form.body" :disabled="form.isWorking"
                       ref="newTaskInput"
                       class="appearance-none bg-gray-300 border border-white focus:outline-none px-3 py-3 rounded text-gray-700 text-lg w-full">
            </form>
        </div>
    </div>
</template>


<script>
    import Form from "../Form";

    export default {
        props: {
            initialTasks: {type: Array, required: false, default: () => []},
            projectId: {type: Number, required: true}
        },
        data() {
            return {
                form: new Form({body: '', completed: false}),
                tasks: this.initialTasks || [],
            }
        },
        directives: {
            'task-focus': (el, binding) => {
                if (binding.value) {
                    el.focus()
                }
            }
        },
        methods: {
            async createTask() {
                const {data} = await this.form.post(`/projects/${this.projectId}/tasks`)
                this.form.reset()
                this.tasks.push(data)
                this.$nextTick(() => {
                    this.$refs.newTaskInput.focus()
                })
            },
        }
    }
</script>

<style scoped lang="less">
    .task-list {
        li .destroy::after {
            content: '×';
        }

        li:hover .destroy {
            display: block;
        }

        li.completed label {
        @apply text-gray-500 line-through;
        }

        li .edit {
            display: none;
        }

        li.editing .view {
            display: none;
        }

        li.editing .edit {
            display: block;
            margin: 0 0 0 43px;
        }
    }
</style>
