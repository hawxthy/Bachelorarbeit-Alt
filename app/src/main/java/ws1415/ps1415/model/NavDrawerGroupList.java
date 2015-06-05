package ws1415.ps1415.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.gc.materialdesign.views.ButtonFlat;

import java.util.List;

import ws1415.ps1415.R;
import ws1415.ps1415.ServiceProvider;
import ws1415.ps1415.activity.CreateUserGroupActivity;
import ws1415.ps1415.activity.FriendsActivity;
import ws1415.ps1415.activity.GroupProfileActivity;
import ws1415.ps1415.activity.ListEventsActivity;
import ws1415.ps1415.activity.ListUserGroupsActivity;
import ws1415.ps1415.activity.MessagingActivity;
import ws1415.ps1415.activity.ProfileActivity;
import ws1415.ps1415.activity.SearchActivity;

/**
 * @author Bernd Eissing on 03.06.2015.
 */
public class NavDrawerGroupList {
    public static final NavDrawerItem[] items = new NavDrawerItem[]{
            // ---------- Gruppe erstellen ----------
            new NavDrawerItem() {
                AlertDialog dialog;
                @Override
                public int getTitleId() {
                    return R.string.create_user_group;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_group;
                }

                @Override
                public void onClick(final AdapterView<?> parent, View view, int position, long id) {
                    // AlertDialog mit variabler Anzahl an Buttons erstellen
                    final AlertDialog.Builder altertadd = new AlertDialog.Builder(parent.getContext());
                    LayoutInflater factory = LayoutInflater.from(parent.getContext());
                    final View functionsView = factory.inflate(R.layout.group_functions, null);

                    // Finde die Buttons und setzt die clicklistener
                    ButtonFlat createGroupButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_create_user_group);
                    createGroupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });
                    ButtonFlat distributeRightsButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_distribute_rights);
                    distributeRightsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });
                    ButtonFlat inviteGroupButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_invite_group);
                    inviteGroupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });
                    ButtonFlat changePrivacyButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_change_group_privacy);
                    changePrivacyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });
                    ButtonFlat changePictureButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_change_group_picture);
                    changePictureButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });
                    ButtonFlat sendGlobalMessageButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_post_global_message);
                    sendGlobalMessageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });
                    ButtonFlat removeMemberButton = (ButtonFlat) functionsView.findViewById(R.id.group_function_remove_member);
                    removeMemberButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent create_group_intent = new Intent(parent.getContext(), CreateUserGroupActivity.class);
                            parent.getContext().startActivity(create_group_intent);
                            altertadd.setCancelable(true);
                            dialog.dismiss();
                        }
                    });

                    // Mache die Buttons unsichtbar, bei denen der User kein Recht hat sie zu benutzen
                    GroupProfileActivity context = (GroupProfileActivity)parent.getContext();
                    List<String> rights = context.getRights();
                    if(!rights.contains(Right.FULLRIGHTS)){
                        if(!rights.contains(Right.DISTRIBUTERIGHTS.name())){
                            distributeRightsButton.setVisibility(View.GONE);
                        }
                        if(!rights.contains(Right.INVITEGROUP.name())){
                            inviteGroupButton.setVisibility(View.GONE);
                        }
                        if(!rights.contains(Right.CHANGEGROUPPRIVACY.name())){
                            changePrivacyButton.setVisibility(View.GONE);
                        }
                        if(!rights.contains(Right.CHANGEGROUPPICTURE.name())){
                            changePictureButton.setVisibility(View.GONE);
                        }
                        if(!rights.contains(Right.GLOBALMESSAGE.name())){
                            sendGlobalMessageButton.setVisibility(View.GONE);
                        }
                        if(!rights.contains(Right.DELETEMEMBER.name())){
                            removeMemberButton.setVisibility(View.GONE);
                        }
                    }


                    altertadd.setView(functionsView);
                    altertadd.setMessage(R.string.groupFunctionsTitle);
                    altertadd.setNegativeButton(R.string.abortGroupFunctions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = altertadd.show();

                }
            },

            // ---------- Veranstaltungen ----------
            new NavDrawerItem() {
                @Override
                public int getTitleId() {
                    return R.string.events;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_event;
                }

                @Override
                public void onClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent show_events_intent = new Intent(parent.getContext(), ListEventsActivity.class);
                    show_events_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    parent.getContext().startActivity(show_events_intent);
                }
            },

            // ---------- Mein Profil ----------
            new NavDrawerItem() {
                @Override
                public int getTitleId() {
                    return R.string.my_profile;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_action_person;
                }

                @Override
                public void onClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent profile_intent = new Intent(parent.getContext(), ProfileActivity.class);
                    profile_intent.putExtra("email", ServiceProvider.getEmail());
                    parent.getContext().startActivity(profile_intent);
                }
            },

            // ---------- Messaging ----------
            new NavDrawerItem() {
                @Override
                public int getTitleId() {
                    return R.string.messages;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_action_chat;
                }

                @Override
                public void onClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent messaging_intent = new Intent(parent.getContext(), MessagingActivity.class);
                    parent.getContext().startActivity(messaging_intent);
                }
            },

            // ---------- Freunde ----------
            new NavDrawerItem() {
                @Override
                public int getTitleId() {
                    return R.string.friends;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_group;
                }

                @Override
                public void onClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent friends_intent = new Intent(parent.getContext(), FriendsActivity.class);
                    parent.getContext().startActivity(friends_intent);
                }
            },

            // ---------- Gruppen ----------
            new NavDrawerItem() {
                @Override
                public int getTitleId() {
                    return R.string.user_groups;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_group;
                }

                @Override
                public void onClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent user_group_intent = new Intent(parent.getContext(), ListUserGroupsActivity.class);
                    user_group_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    parent.getContext().startActivity(user_group_intent);
                }
            },

            // ---------- Suche ----------
            new NavDrawerItem() {
                @Override
                public int getTitleId() {
                    return R.string.search;
                }

                @Override
                public int getIconId() {
                    return R.drawable.ic_action_search;
                }

                @Override
                public void onClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent search_intent = new Intent(parent.getContext(), SearchActivity.class);
                    search_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    parent.getContext().startActivity(search_intent);
                }
            },
    };
}